package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public enum DeclareBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams, boolean isJob) {
		final var environment = shell.getEnvironment();

		final var flag = arguments.get(1);
		if ("-p".equals(flag)) {
			final var name = arguments.get(2);

			final var value = environment.getVariable(name);
			if (value != null) {
				redirectStreams.error().printf("%s -- %s=\"%s\"", arguments.get(0), name, value);
			} else {
				redirectStreams.error().printf("%s: %s: not found", arguments.get(0), name);
			}
		} else if (!flag.startsWith("-")) {
			final var parts = flag.split("=", 2);
			final var name = parts[0];
			final var value = parts[1];

			environment.setVariable(name, value);
		}

		return OptionalInt.empty();
	}

}