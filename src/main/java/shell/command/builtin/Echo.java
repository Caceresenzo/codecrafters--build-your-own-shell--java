package shell.command.builtin;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Echo implements Builtin {

	@Override
	public void execute(String[] arguments) {
		final var line = Arrays.stream(arguments)
			.skip(1)
			.collect(Collectors.joining(" "));

		System.out.println(line);
	}

}