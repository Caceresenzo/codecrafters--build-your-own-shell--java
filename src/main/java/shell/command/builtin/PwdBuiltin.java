package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public enum PwdBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		redirectStreams.output().println(shell.getWorkingDirectory().toString());

		return OptionalInt.empty();
	}

}