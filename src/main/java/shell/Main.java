package shell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lombok.SneakyThrows;
import shell.command.Binary;
import shell.command.builtin.Builtin;
import shell.io.RedirectStreams;
import shell.parse.LineParser;
import shell.parse.ParsedCommand;
import shell.terminal.Termios;

public class Main {

	public static final String BUILTIN_OPTION = "--builtin";

	private static final char UP = 'A';
	private static final char DOWN = 'B';

	@SneakyThrows
	public static void main(String[] args) {
		int exitCode = 0;

		try (
			final var shell = new Shell()
		) {
			final var builtinOption = new Option(null, BUILTIN_OPTION.substring(2), true, "run a builtin");
			builtinOption.setArgs(Option.UNLIMITED_VALUES);

			final var options = new Options()
				.addOption(builtinOption);

			final var cli = new DefaultParser().parse(options, args);

			if (cli.hasOption(builtinOption)) {
				final var arguments = new ArrayList<>(Arrays.asList(cli.getOptionValues(builtinOption)));

				final var name = arguments.removeFirst();
				final var builtin = shell.whichBuiltin(name);
				if (builtin == null) {
					notFound(name);
					System.exit(1);
				}

				builtin.execute(shell, arguments, RedirectStreams.standard(), false);
				return;
			}

			exitCode = loop(shell);
		}

		System.exit(exitCode);
	}

	public static int loop(Shell shell) {
		while (true) {
			shell.getJobs().reap(false);

			final var line = read(shell);

			if (line == null) {
				break;
			} else if (line.isBlank()) {
				continue;
			} else {
				final var shellExitCode = eval(shell, line);

				if (shellExitCode.isPresent()) {
					return shellExitCode.getAsInt();
				}
			}
		}

		return 0;
	}

	public static int prompt() {
		final var prompt = "$ ";
		System.out.print(prompt);
		return prompt.length();
	}

	public static void bell() {
		System.out.print((char) 0x7);
	}

	@SneakyThrows
	public static String read(Shell shell) {
		final var completer = shell.getCompleter();

		final var line = new StringBuilder();

		var promptLength = 0;
		var bellRang = false;

		final var historyLines = shell.getHistory();
		var historyPosition = historyLines.size();

		try (final var scope = Termios.enableRawMode()) {
			promptLength = prompt();

			while (true) {
				final var input = System.in.read();
				if (input == -1) {
					return null;
				}

				final var character = (char) input;
				switch (character) {
					case 0x4: {
						if (!line.isEmpty()) {
							continue;
						}

						return null;
					}

					case '\r': {
						break; /* ignore */
					}

					case '\n': {
						System.out.print('\n');

						return line.toString();
					}

					case '\t': {
						switch (completer.complete(shell, line, bellRang)) {
							case NONE -> {
								bellRang = false;
								bell();
							}
							case FOUND -> {
								bellRang = false;
							}
							case MORE -> {
								bellRang = true;
								bell();
							}
						};

						break;
					}

					case 0x1b: {
						System.in.read(); // '['

						final var direction = System.in.read();
						if (UP == direction && historyPosition != 0) {
							--historyPosition;
							promptLength = changeLine(line, historyLines.get(historyPosition), promptLength);
						} else if (DOWN == direction && historyPosition < historyLines.size()) {
							++historyPosition;

							if (historyPosition == historyLines.size()) {
								promptLength = changeLine(line, "", promptLength);
							} else {
								promptLength = changeLine(line, historyLines.get(historyPosition), promptLength);
							}
						}

						break;
					}

					case 0x7f: {
						if (line.isEmpty()) {
							continue;
						}

						line.setLength(line.length() - 1);

						System.out.print("\b \b");

						break;
					}

					default: {
						line.append(character);

						System.out.print(character);

						break;
					}
				}
			}
		}
	}

	private static int changeLine(StringBuilder currentLine, String newLine, int promptLength) {
		final var clear = "\r" + " ".repeat(currentLine.length() + promptLength) + "\r";
		System.out.print(clear);

		promptLength = prompt();
		System.out.print(newLine);

		currentLine.setLength(0);
		currentLine.append(newLine);

		return promptLength;
	}

	@SneakyThrows
	public static OptionalInt eval(Shell shell, String line) {
		final var environment = shell.getEnvironment();

		shell.getHistory().add(line);

		final var commands = new LineParser(line).parse();

		if (commands.isEmpty()) {
			return null;
		} else if (commands.size() == 1) {
			final var command = commands.getFirst();

			final var arguments = command.resolveArguments(environment);
			final var program = arguments.getFirst();

			final var executable = shell.which(program);
			if (executable != null) {
				try (final var redirectStreams = RedirectStreams.from(command.redirects(), environment)) {
					return executable.execute(shell, arguments, redirectStreams, command.isJob());
				}
			} else {
				notFound(program);
			}
		} else {
			pipeline(shell, commands);
		}

		return OptionalInt.empty();
	}

	private static void notFound(String program) {
		System.err.println("%s: command not found".formatted(program));
	}

	@SneakyThrows
	private static void pipeline(Shell shell, List<ParsedCommand> commands) {
		final var environment = shell.getEnvironment();

		final var currentProcessInfo = ProcessHandle.current().info();
		final var jvmCommand = currentProcessInfo.command().orElse("java");
		final var jvmArguments = currentProcessInfo.arguments().map(Arrays::asList).orElse(Collections.emptyList());

		final var streams = new ArrayList<RedirectStreams>();
		final var builders = new ArrayList<ProcessBuilder>();

		try {
			for (var index = 0; index < commands.size(); index++) {
				final var isFirst = index == 0;
				final var isLast = index == commands.size() - 1;

				final var command = commands.get(index);

				final var arguments = command.resolveArguments(environment);
				final var program = arguments.getFirst();
				final var executable = shell.which(program);

				final var commandArguments = switch (executable) {

					case Builtin __: {
						final var newArguments = new ArrayList<String>();
						newArguments.add(jvmCommand);
						newArguments.addAll(jvmArguments);
						newArguments.add(BUILTIN_OPTION);
						newArguments.add(program);
						newArguments.addAll(arguments);

						yield newArguments;
					}

					case Binary(var path): {
						yield Stream
							.concat(
								/* stupid but java does not allow custom arg0 */
								Stream.of(path.getFileName().toString()),
								arguments.stream().skip(1)
							)
							.toList();
					}

					case null: {
						final var newArguments = new ArrayList<String>();
						newArguments.add(jvmCommand);
						newArguments.addAll(jvmArguments);
						newArguments.add(BUILTIN_OPTION);
						newArguments.add(program);

						yield newArguments;
					}

					default: {
						throw new IllegalArgumentException("unexpected executable: " + executable);
					}

				};

				final var builder = new ProcessBuilder(commandArguments)
					.inheritIO()
					.directory(shell.getWorkingDirectory().toFile())
					.redirectInput(isFirst ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE)
					.redirectOutput(isLast ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE)
					.redirectError(ProcessBuilder.Redirect.INHERIT);

				final var redirectStreams = RedirectStreams.from(command.redirects(), environment);
				streams.add(redirectStreams);
				redirectStreams.apply(builder);

				builders.add(builder);
			}

			final var processes = ProcessBuilder.startPipeline(builders);
			for (final var process : processes) {
				process.waitFor();
			}
		} finally {
			for (final var redirectStreams : streams) {
				redirectStreams.close();
			}
		}
	}

}
