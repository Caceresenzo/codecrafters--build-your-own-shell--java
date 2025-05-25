package shell.command.builtin;

import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

public enum History implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		var lines = shell.getHistory();

		final var start = Math.max(0, arguments.size() < 2 ? 0 : Integer.parseInt(arguments.get(1)));
		if (start != 0) {
			final var size = lines.size();
			lines = lines.subList(size - start, size);
		}

		var index = start;
		for (final var line : lines) {
			++index;
			redirectStreams.output().println("%5d  %s".formatted(index, line));
		}
	}

}