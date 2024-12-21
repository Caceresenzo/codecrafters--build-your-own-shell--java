package shell.command.builtin;

import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

public enum Pwd implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		redirectStreams.output().println(shell.getWorkingDirectory().toString());
	}

}