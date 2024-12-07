package shell.command.builtin;

import shell.Shell;
import shell.command.Executable;

public enum Type implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, String[] arguments) {
		final var program = arguments[1];
		final var command = shell.find(program);

		if (command instanceof Builtin) {
			System.out.println("%s is a shell builtin".formatted(program));
		} else if (command instanceof Executable(final var path)) {
			System.out.println("%s is %s".formatted(program, path));
		} else {
			System.out.println("%s: not found".formatted(program));
		}
	}

}