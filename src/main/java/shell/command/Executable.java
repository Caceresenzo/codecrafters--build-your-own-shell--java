package shell.command;

import java.nio.file.Path;

public record Executable(
	Path path
) implements Command {

	@Override
	public void execute(String[] arguments) {
		throw new UnsupportedOperationException();
	}

}