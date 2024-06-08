package shell.command.builtin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import shell.Shell;

@RequiredArgsConstructor
public class Pwd implements Builtin {

	private final @NonNull Shell shell;

	@Override
	public void execute(String[] arguments) {
		System.out.println(shell.getWorkingDirectory());
	}

}