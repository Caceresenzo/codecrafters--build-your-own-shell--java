package shell.command;

import shell.Shell;

public interface Command {

	void execute(Shell shell, String[] arguments);

}