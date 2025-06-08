package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public enum ExitBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var exitCode = arguments.size() > 2
			? Integer.parseInt(arguments.get(1))
			: 0;

		return OptionalInt.of(exitCode);
	}

}