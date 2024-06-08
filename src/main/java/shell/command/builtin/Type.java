package shell.command.builtin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import shell.Shell;
import shell.command.Executable;

@RequiredArgsConstructor
public class Type implements Builtin {

	private final @NonNull Shell shell;

	@Override
	public void execute(String[] arguments) {
		final var program = arguments[1];
		final var command = shell.find(program);

		if (command instanceof Builtin) {
			System.out.println("%s is a shell builtin".formatted(program));
		} else if (command instanceof Executable(final var path)) {
			System.out.println("%s is %s".formatted(program, path));
		} else {
			System.out.println("%s not found".formatted(program));
		}
	}

}