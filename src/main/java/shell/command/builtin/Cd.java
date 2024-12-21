package shell.command.builtin;

import java.nio.file.Path;
import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

public enum Cd implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var path = arguments.get(1);
		final var absolute = toAbsolute(shell, path).normalize().toAbsolutePath();

		if (!shell.changeWorkingDirectory(absolute)) {
			redirectStreams.error().println("cd: %s: No such file or directory".formatted(absolute));
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