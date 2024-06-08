package shell.command.builtin;

import java.nio.file.Path;

import shell.Shell;

public enum Cd implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, String[] arguments) {
		final var path = arguments[1];
		final var absolute = toAbsolute(shell, path).normalize().toAbsolutePath();

		if (!shell.changeWorkingDirectory(absolute)) {
			System.out.println("cd: %s: No such file or directory".formatted(absolute));
		}
	}

	public Path toAbsolute(Shell shell, String input) {
		if (input.startsWith("/")) {
			return Path.of(input);
		}

		if (input.startsWith(".")) {
			return shell.getWorkingDirectory().resolve(input);
		}

		if (input.startsWith("~")) {
			final var home = System.getenv("HOME");
			if (home == null) {
				throw new UnsupportedOperationException("$HOME is not defined");
			}

			return Path.of(home, input.substring(1));
		}

		throw new UnsupportedOperationException("path `%s`".formatted(input));
	}

}