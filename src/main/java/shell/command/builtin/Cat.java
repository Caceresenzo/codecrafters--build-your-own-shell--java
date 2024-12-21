package shell.command.builtin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

/* https://forum.codecrafters.io/t/shell-redirection-stage-jv1-first-cannot-be-completed-due-to-a-different-error-message/3585 */
public enum Cat implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams) {
		final var files = arguments.stream().skip(1).toList();

		for (final var file : files) {
			catFile(shell, redirectStreams, file);
		}
	}

	private void catFile(Shell shell, RedirectStreams redirectStreams, final String file) {
		final var path = shell.getWorkingDirectory().resolve(file);

		try (final var inputStream = new FileInputStream(path.toFile())) {
			final var buffer = new byte[1024];

			var read = 0;
			while ((read = inputStream.read(buffer)) != -1) {
				redirectStreams.output().write(buffer, 0, read);
			}
		} catch (FileNotFoundException exception) {
			redirectStreams.error().println("cat: %s: No such file or directory".formatted(file));
		} catch (Exception exception) {
			redirectStreams.error().println("cat: %s: %s".formatted(exception.getMessage()));
		}
	}

	@Override
	public boolean acceptForType() {
		return false;
	}

}