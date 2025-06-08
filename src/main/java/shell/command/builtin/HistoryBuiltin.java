package shell.command.builtin;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public enum HistoryBuiltin implements Builtin {

	INSTANCE;

	@Override
	public OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var first = arguments.size() > 1 ? arguments.get(1) : null;

		if (first != null && first.matches("[\\d]+")) {
			final var start = shell.getHistory().size() - Integer.parseInt(arguments.get(1));

			print(shell, redirectStreams, start);
		} else if ("-r".equals(first)) {
			final var path = shell.getWorkingDirectory().resolve(arguments.get(2));
			shell.getHistory().readFrom(path);
		} else if ("-w".equals(first)) {
			final var path = shell.getWorkingDirectory().resolve(arguments.get(2));
			shell.getHistory().writeTo(path);
		} else if ("-a".equals(first)) {
			final var path = shell.getWorkingDirectory().resolve(arguments.get(2));
			shell.getHistory().appendTo(path);
		} else {
			print(shell, redirectStreams, 0);
		}

		return OptionalInt.empty();
	}

	public void print(Shell shell, RedirectStreams redirectStreams, int start) {
		var history = shell.getHistory();

		for (var index = start; index < history.size(); ++index) {
			final var line = history.get(index);

			redirectStreams.output().println("%5d  %s".formatted(index, line));
		}
	}

}