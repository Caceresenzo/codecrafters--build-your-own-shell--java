package shell.command.builtin;

import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

public enum History implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var lines = shell.getHistory();

		var index = 0;
		for (final var line : lines) {
			++index;
			redirectStreams.output().println("%5d  %s".formatted(index, line));
		}
	}

}