package shell.command.builtin;

import java.util.List;

import shell.Shell;
import shell.command.Binary;
import shell.io.RedirectStreams;

public enum Type implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var program = arguments.get(1);
		final var command = shell.which(program);

		if (command instanceof Builtin) {
			redirectStreams.output().println("%s is a shell builtin".formatted(program));
		} else if (command instanceof Binary(final var path)) {
			redirectStreams.output().println("%s is %s".formatted(program, path));
		} else {
			redirectStreams.output().println("%s: not found".formatted(program));
		}
	}

}