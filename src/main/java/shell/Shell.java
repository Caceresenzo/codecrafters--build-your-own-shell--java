package shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import shell.command.Binary;
import shell.command.Executable;
import shell.command.builtin.Builtin;
import shell.command.builtin.Cd;
import shell.command.builtin.Echo;
import shell.command.builtin.Exit;
import shell.command.builtin.History;
import shell.command.builtin.Pwd;
import shell.command.builtin.Type;

public class Shell {

	public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

	@Getter
	public final Map<String, Builtin> builtins = Map.of(
		"exit", Exit.INSTANCE,
		"echo", Echo.INSTANCE,
		"type", Type.INSTANCE,
		"pwd", Pwd.INSTANCE,
		"cd", Cd.INSTANCE,
		"history", History.INSTANCE
	);

	private @Getter Path workingDirectory = Path.of(".").toAbsolutePath().normalize();
	private @Getter List<String> history = new ArrayList<>();

	public Builtin whichBuiltin(String name) {
		return builtins.get(name);
	}

	public Executable which(String program) {
		final var builtin = whichBuiltin(program);
		if (builtin != null) {
			return builtin;
		}

		if (IS_WINDOWS) {
			program = program.replace('\\', '/');
		}

		for (final var directory : get$PATH()) {
			final var path = Paths.get(directory, program).normalize().toAbsolutePath();

			if (Files.exists(path)) {
				return new Binary(path);
			}
		}

		return null;
	}

	public boolean changeWorkingDirectory(Path path) {
		if (!Files.exists(path)) {
			return false;
		}

		workingDirectory = path;
		return true;
	}

	public String[] get$PATH() {
		final var separator = IS_WINDOWS ? ";" : ":";

		return System.getenv("PATH").split(separator);
	}

}