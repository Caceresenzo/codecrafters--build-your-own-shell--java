package shell;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import lombok.Getter;
import shell.command.Binary;
import shell.command.Executable;
import shell.command.builtin.Builtin;
import shell.command.builtin.CdBuiltin;
import shell.command.builtin.EchoBuiltin;
import shell.command.builtin.ExitBuiltin;
import shell.command.builtin.HistoryBuiltin;
import shell.command.builtin.PwdBuiltin;
import shell.command.builtin.TypeBuiltin;
import shell.history.History;

public class Shell {

	public static final boolean IS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

	@Getter
	public final Map<String, Builtin> builtins = Map.of(
		"exit", ExitBuiltin.INSTANCE,
		"echo", EchoBuiltin.INSTANCE,
		"type", TypeBuiltin.INSTANCE,
		"pwd", PwdBuiltin.INSTANCE,
		"cd", CdBuiltin.INSTANCE,
		"history", HistoryBuiltin.INSTANCE
	);

	private @Getter Path workingDirectory = Path.of(".").toAbsolutePath().normalize();
	private @Getter History history = new History(this);

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