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
		
		final var first = arguments.size() > 1 ? arguments.get(1) : null;
		
		if (first != null && first.matches("[\\d]+")) {
			start = history.size() - Integer.parseInt(arguments.get(1));
		} else if ("-r".equals(first)) {
			final var path = shell.getWorkingDirectory().resolve(arguments.get(2));
			shell.getHistory().readFrom(path);
			
			return;
		}

		for (var index = start; index < history.size(); ++index) {
			final var line = history.get(index);

			redirectStreams.output().println("%5d  %s".formatted(index, line));
		}
	}

}