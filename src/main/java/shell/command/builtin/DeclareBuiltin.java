package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public enum DeclareBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams, boolean isJob) {
		final var flag = arguments.get(1);

		if ("-p".equals(flag)) {
			final var name = arguments.get(2);

			redirectStreams.error().printf("%s: %s: not found", arguments.get(0), name);
		}

		return OptionalInt.empty();
	}

}