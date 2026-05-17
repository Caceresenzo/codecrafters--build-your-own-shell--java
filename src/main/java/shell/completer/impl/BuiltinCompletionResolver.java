package shell.completer.impl;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import shell.Shell;
import shell.completer.CompletionResolver;

public enum BuiltinCompletionResolver implements CompletionResolver {

	INSTANCE;

	@Override
	public Set<String> getCompletions(Shell shell, Path directory, String command, String prefix) {
		return shell.getBuiltins()
			.keySet()
			.stream()
			.filter((name) -> name.startsWith(prefix))
			.collect(Collectors.toSet());
	}

}