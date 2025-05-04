package shell.io;

import java.io.FileNotFoundException;
import java.util.List;

import shell.parse.Redirect;

public record RedirectStreams(
	RedirectStream output,
	RedirectStream error
) implements AutoCloseable {

	@Override
	public void close() {
		try (output; error) {}
	}

	public void apply(ProcessBuilder builder) {
		applyRedirect(builder, output, StandardNamedStream.OUTPUT);
		applyRedirect(builder, error, StandardNamedStream.ERROR);
	}

	private void applyRedirect(ProcessBuilder builder, RedirectStream stream, StandardNamedStream streamName) {
		final var isStderr = StandardNamedStream.ERROR.equals(streamName);

		switch (stream) {
			case RedirectStream.Standard standard -> {
				if (isStderr && StandardNamedStream.OUTPUT.equals(standard.name())) {
					builder.redirectErrorStream(true);
				}
			}

			case RedirectStream.File file -> {
				file.close();

				final var redirect = file.append()
					? ProcessBuilder.Redirect.appendTo(file.path().toFile())
					: ProcessBuilder.Redirect.to(file.path().toFile());

				if (isStderr) {
					builder.redirectError(redirect);
				} else {
					builder.redirectOutput(redirect);
				}
			}
		}
	}

	public static RedirectStreams standard() {
		return new RedirectStreams(
			new RedirectStream.Standard(StandardNamedStream.OUTPUT, System.out),
			new RedirectStream.Standard(StandardNamedStream.ERROR, System.err)
		);
	}

	public static RedirectStreams from(List<Redirect> redirects) throws FileNotFoundException {
		RedirectStream output = new RedirectStream.Standard(StandardNamedStream.OUTPUT, System.out);
		RedirectStream error = new RedirectStream.Standard(StandardNamedStream.ERROR, System.err);

		for (final var redirect : redirects) {
			final var stream = new RedirectStream.File(redirect.path(), redirect.append());

			switch (redirect.namedStream()) {
				case OUTPUT -> {
					output.close();
					output = stream;
				}

				case ERROR -> {
					error.close();
					error = stream;
				}

				case UNKNOWN -> {
					stream.close();
				}
			}
		}

		return new RedirectStreams(output, error);
	}

}