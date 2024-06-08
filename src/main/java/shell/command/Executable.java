package shell.command;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import lombok.SneakyThrows;

public record Executable(
	Path path
) implements Command {

	@SneakyThrows
	@Override
	public void execute(String[] arguments) {
		try {
			final var commandArguments = Stream
				.concat(
					Stream.of(path.toString()),
					Arrays.stream(arguments).skip(1)
				)
				.toList();

			final var process = new ProcessBuilder(commandArguments).start();
			process.waitFor();
		} catch (Exception exception) {
			System.err.println(exception.getMessage());
		}
	}

}