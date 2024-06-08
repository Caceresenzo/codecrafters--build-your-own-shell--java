package shell.command.builtin;

import java.nio.file.Path;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import shell.Shell;

@RequiredArgsConstructor
public class Cd implements Builtin {

	private final @NonNull Shell shell;

	@Override
	public void execute(String[] arguments) {
		final var path = arguments[1];
		final var absolute = toAbsolute(path).normalize().toAbsolutePath();

		if (!shell.changeWorkingDirectory(absolute)) {
			System.out.println("cd: %s: No such file or directory".formatted(absolute));
		}
	}

	public Path toAbsolute(String input) {
		if (input.startsWith("/")) {
			return Path.of(input);
		}

		if (input.startsWith(".")) {
			return shell.getWorkingDirectory().resolve(input);
		}

		throw new UnsupportedOperationException("path `%s`".formatted(input));
	}

}