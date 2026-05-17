package shell.completer.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.SneakyThrows;
import shell.Shell;
import shell.completer.CompletionResolver;
import shell.parse.LineParser;

public class CustomCompletionResolver implements CompletionResolver {

	private final Map<String, String> handlers = new HashMap<>();

	@SneakyThrows
	@Override
	public Set<String> getCompletions(Shell shell, String line, Path directory, String prefix) {
		final var parsedCommand = new LineParser(line).parse().getLast();
		final var arguments = parsedCommand.arguments();

		final var environment = shell.getEnvironment();
		final var zeroArgument = !arguments.isEmpty() ? arguments.get(0).resolve(environment) : "";

		final var handlerPath = getHandler(zeroArgument);
		if (handlerPath.isEmpty() || arguments.size() == 1 && !line.endsWith(" ")) {
			return Set.of();
		}

		final var lastArgument = arguments.size() > 1 ? arguments.get(arguments.size() - 1).resolve(environment) : "";
		final var previousArgument = arguments.size() > 2 ? arguments.get(arguments.size() - 2).resolve(environment) : "";

		return runHandler(handlerPath.get(), zeroArgument, lastArgument, previousArgument);
	}

	private Set<String> runHandler(String handlerPath, String zeroArgument, String lastArgument, String previousArgument, String originalLine) throws IOException {
		final var candidates = new HashSet<String>();

		final var environment = new HashMap<String, String>(System.getenv());
		environment.put("COMP_LINE", originalLine);
		environment.put("COMP_POINT", String.valueOf(originalLine.length()));

		final var argv = new String[] {
			handlerPath,
			zeroArgument,
			lastArgument,
			previousArgument
		};

		final var envp = environment.entrySet()
			.stream()
			.map((entry) -> entry.getKey() + "=" + entry.getValue())
			.toArray(String[]::new);

		final var process = Runtime.getRuntime().exec(argv, envp);
		final var stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line = null;
		while ((line = stdout.readLine()) != null) {
			line = line.trim();

			if (!line.isEmpty()) {
				candidates.add(line);
			}
		}

		return candidates;
	}

	public void registerHandler(String programName, String command) {
		handlers.put(programName, command);
	}

	public Optional<String> getHandler(String programName) {
		return Optional.ofNullable(handlers.get(programName));
	}

}