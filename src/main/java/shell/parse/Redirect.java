package shell.parse;

import shell.io.StandardNamedStream;

public record Redirect(
	StandardNamedStream namedStream,
	Argument path,
	boolean append
) {

	public boolean isFile() {
		return path != null;
	}

	public void println(String message) {
		final var printStream = namedStream.getPrintStream();

		if (printStream != null) {
			printStream.println(message);
		}
	}

}