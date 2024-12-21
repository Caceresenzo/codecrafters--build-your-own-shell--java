package shell.command.builtin;

import shell.command.Command;

public interface Builtin extends Command {

	default boolean acceptForType() {
		return true;
	}

}