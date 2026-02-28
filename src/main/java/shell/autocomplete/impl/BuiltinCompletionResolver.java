package shell.autocomplete.impl;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import shell.Shell;
import shell.autocomplete.CompletionResolver;

public enum BuiltinCompletionResolver implements CompletionResolver {

	INSTANCE;

	@Override
	public Set<String> getCompletions(Shell shell, boolean isCommand, Path directory, String prefix) {
		if (!isCommand) {
			return Set.of();
		}

		return shell.getBuiltins()
			.keySet()
			.stream()
			.filter((name) -> name.startsWith(prefix))
			.collect(Collectors.toSet());
	}

}