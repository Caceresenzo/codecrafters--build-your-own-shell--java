package shell.command.builtin;

import shell.Shell;

public enum Pwd implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, String[] arguments) {
		System.out.println(shell.getWorkingDirectory());
	}

}