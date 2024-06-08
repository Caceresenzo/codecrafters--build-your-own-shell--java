package shell.command.builtin;

import java.util.Arrays;
import java.util.stream.Collectors;

import shell.Shell;

public enum Echo implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, String[] arguments) {
		final var line = Arrays.stream(arguments)
			.skip(1)
			.collect(Collectors.joining(" "));

		System.out.println(line);
	}

}