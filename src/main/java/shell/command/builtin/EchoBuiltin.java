package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import shell.Shell;
import shell.io.RedirectStreams;

public enum EchoBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var line = arguments.stream()
			.skip(1)
			.collect(Collectors.joining(" "));

		redirectStreams.output().println(line);

		return OptionalInt.empty();
	}

}