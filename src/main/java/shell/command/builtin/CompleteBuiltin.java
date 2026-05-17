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
			case "-C" -> {
				final var completerPath = arguments.get(2);
				final var programName = arguments.get(3);

				shell.getCompleter().getCustomCompletionResolver().registerHandler(programName, completerPath);
			}

			case "-p" -> {
				final var programName = arguments.get(2);

				final var handlerPath = shell.getCompleter().getCustomCompletionResolver().getHandler(programName);
				if (handlerPath.isPresent()) {
					redirectStreams.output().println("%s -C '%s' %s", completeProgramName, handlerPath.get(), programName);
				} else {
					redirectStreams.error().println("%s: %s: no completion specification", completeProgramName, programName);
				}
			}

			default -> {
				redirectStreams.error().println("%s: unknown flag: %s", completeProgramName, flag);
			}
		}

		return OptionalInt.empty();
	}

}