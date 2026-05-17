package shell.completer.impl;

import java.io.BufferedReader;
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

public class CustomCompletionResolver implements CompletionResolver {

	private final Map<String, String> handlers = new HashMap<>();

	@SneakyThrows
	@Override
	public Set<String> getCompletions(Shell shell, Path directory, String command, String prefix) {
		final var handlerPath = getHandler(command);
		if (handlerPath.isEmpty()) {
			return Set.of();
		}

		final var candidates = new HashSet<String>();

		final var process = Runtime.getRuntime()
			.exec(new String[] { handlerPath.get(), prefix });

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