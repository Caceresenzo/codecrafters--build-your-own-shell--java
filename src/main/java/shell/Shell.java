package shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import lombok.Getter;
import shell.command.Command;
import shell.command.Executable;
import shell.command.builtin.Builtin;
import shell.command.builtin.Echo;
import shell.command.builtin.Exit;
import shell.command.builtin.Pwd;
import shell.command.builtin.Type;

public class Shell {

	public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

	public final Map<String, Builtin> builtins = Map.of(
		"exit", new Exit(),
		"echo", new Echo(),
		"type", new Type(this),
		"pwd", new Pwd(this)
	);

	private @Getter Path workingDirectory = Path.of(".").toAbsolutePath().normalize();

	public Command find(String program) {
		final var builtin = builtins.get(program);
		if (builtin != null) {
			return builtin;
		}

		final var separator = IS_WINDOWS ? ";" : ":";
		final var paths = System.getenv("PATH").split(separator);

		for (final var directory : paths) {
			final var path = Paths.get(directory, program).normalize().toAbsolutePath();

			if (Files.exists(path)) {
				return new Executable(path);
			}
		}

		return null;
	}

}