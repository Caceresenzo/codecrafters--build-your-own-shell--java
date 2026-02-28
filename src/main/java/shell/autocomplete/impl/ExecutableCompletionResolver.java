package shell.autocomplete.impl;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import shell.Shell;
import shell.autocomplete.CompletionResolver;

public enum ExecutableCompletionResolver implements CompletionResolver {

	INSTANCE;

	@Override
	public Set<String> getCompletions(Shell shell, boolean isCommand, Path directory, String prefix) {
		if (!isCommand) {
			return Set.of();
		}

		final var candidates = new HashSet<String>();

		final FileFilter filter = (file) -> {
			return file.getName().startsWith(prefix) && file.isFile() && file.canExecute();
		};

		for (final var path : shell.get$PATH()) {
			final var files = new File(path).listFiles(filter);
			if (files == null) {
				continue;
			}

			for (final var file : files) {
				candidates.add(file.getName());
			}
		}

		return candidates;
	}

}