package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Environment;
import shell.Shell;
import shell.io.RedirectStreams;

public enum DeclareBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams, boolean isJob) {
		final var programName = arguments.get(0);
		final var environment = shell.getEnvironment();

		final var flag = arguments.get(1);
		if ("-p".equals(flag)) {
			final var name = arguments.get(2);

			final var value = environment.getVariable(name);
			if (value != null) {
				redirectStreams.error().println("%s -- %s=\"%s\"", programName, name, value);
			} else {
				redirectStreams.error().println("%s: %s: not found", programName, name);
			}
		} else if (!flag.startsWith("-")) {
			final var parts = flag.split("=", 2);
			final var name = parts[0];
			final var value = parts[1];

			if (!Environment.isValidName(name)) {
				redirectStreams.error().println("%s: `%s=%s': not a valid identifier", programName, name, value);
				return OptionalInt.empty();
			}

			environment.setVariable(name, value);
		}

		return OptionalInt.empty();
	}

}