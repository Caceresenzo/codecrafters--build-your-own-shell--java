package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public enum CompleteBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams, boolean isJob) {
		final var completeProgramName = arguments.get(0);
		final var flag = arguments.get(1);

		switch (flag) {
			case "-p" -> {
				final var programName = arguments.get(2);
				redirectStreams.error().println("%s: %s: no completion specification", completeProgramName, programName);
			}

			default -> {
				redirectStreams.error().println("%s: unknown flag: %s", completeProgramName, flag);
			}
		}

		return OptionalInt.empty();
	}

}