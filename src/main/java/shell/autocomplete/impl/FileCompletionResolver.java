package shell.autocomplete.impl;

import java.io.FileFilter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import shell.Shell;
import shell.autocomplete.CompletionResolver;

public enum FileCompletionResolver implements CompletionResolver {

	INSTANCE;

	@Override
	public Set<String> getCompletions(Shell shell, boolean isCommand, Path directory, String prefix) {
		final FileFilter filter = (file) -> {
			//			System.err.println("filtering: " + file);
			return file.getName().startsWith(prefix);
		};

		//		System.err.println("listing: " + directory);
		final var files = directory.toFile().listFiles(filter);
		if (files == null) {
			return Set.of();
		}

		final var candidates = new HashSet<String>();
		for (final var file : files) {
			var name = file.getName();
			if (file.isDirectory()) {
				name += "/";
			}

			candidates.add(name);
		}

		return candidates;
	}

}