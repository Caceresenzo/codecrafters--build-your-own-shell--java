package shell.command.builtin;

import shell.Shell;

public enum Exit implements Builtin {

	INSTANCE;

	@Override
	public void execute(Shell shell, String[] arguments) {
		System.exit(0);
	}

}