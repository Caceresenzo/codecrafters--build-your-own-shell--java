package shell;

import lombok.SneakyThrows;
import shell.io.RedirectStreams;
import shell.parse.LineParser;
import shell.terminal.Terminal;

public class Main {

	public static void main(String[] args) throws Exception {
		Shell shell = new Shell();

		while (true) {
			final var line = read();

			if (line == null) {
				break;
			} else if (line.isBlank()) {
				continue;
			} else {
				eval(shell, line);
			}
		}
	}

	public static void prompt() {
		System.out.print("$ ");
		//		System.out.flush();
	}

	@SneakyThrows
	public static String read() {
		final var line = new StringBuilder();

		try (final var input = new Terminal()) {
			prompt();

			while (true) {
				char character = input.read();
				if (character == 0) {
					return null;
				}
//				System.err.printf("`%d`", (int) character);

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
//						Terminal.write("\n");

						return line.toString();
					}

					case 0x1b: {
						input.read(); // '['
						input.read(); // 'A' or 'B' or 'C' or 'D'

						break;
					}

					case 0x7f: {
						if (line.isEmpty()) {
							continue;
						}

						line.setLength(line.length() - 1);

						Terminal.write("\b \b");

						break;
					}

					default: {
						line.append(character);

//						Terminal.write(String.valueOf(character));

						break;
					}
				}
			}
		}
	}

	@SneakyThrows
	public static void eval(Shell shell, String line) {
		final var parsedLine = new LineParser(line).parse();

		final var arguments = parsedLine.arguments();
		final var program = arguments.getFirst();

		final var command = shell.find(program);
		if (command != null) {
			try (final var redirectStreams = RedirectStreams.from(parsedLine.redirects())) {
				command.execute(shell, arguments, redirectStreams);
			}
		} else {
			System.out.println("%s: command not found".formatted(program));
		}
	}

}
