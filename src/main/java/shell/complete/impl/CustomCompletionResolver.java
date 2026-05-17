package shell.complete.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import shell.Shell;
import shell.complete.CompletionResolver;

public class CustomCompletionResolver implements CompletionResolver {

	private final Map<String, String> handlers = new HashMap<>();

	@Override
	public Set<String> getCompletions(Shell shell, boolean isCommand, Path directory, String prefix) {
		return Set.of();
	}

	public void registerHandler(String programName, String command) {
		handlers.put(programName, command);
	}

	public Optional<String> getHandler(String programName) {
		return Optional.ofNullable(handlers.get(programName));
	}

}