package shell.command.builtin;

import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

public enum HistoryBuiltin implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		var history = shell.getHistory();

		var start = 0;
		if (arguments.size() > 1) {
			start = history.size() - Integer.parseInt(arguments.get(1));
		}

		for (var index = start; index < history.size(); ++index) {
			final var line = history.get(index);

			redirectStreams.output().println("%5d  %s".formatted(index, line));
		}
	}

}