package shell.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

public sealed interface RedirectStream extends AutoCloseable {

	void println(String message);

	@Override
	void close();

	public record Standard(
		StandardNamedStream name,
		PrintStream print
	) implements RedirectStream {

		@Override
		public void println(String message) {
			print.println(message);
		}

		@Override
		public void close() {}

	}

	@Getter
	@Accessors(fluent = true)
	@ToString
	public final class File implements RedirectStream {

		private final Path path;
		private final boolean append;
		private final PrintWriter writer;

		public File(Path path, boolean append) throws FileNotFoundException {
			this.path = path;
			this.append = append;
			this.writer = new PrintWriter(new FileOutputStream(path.toFile(), append), true);
		}

		@Override
		public void println(String message) {
			writer.println(message);
		}

		@Override
		public void close() {
			writer.close();
		}

	}

}