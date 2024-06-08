package shell.command.builtin;

public class Exit implements Builtin {

	@Override
	public void execute(String[] arguments) {
		System.exit(0);
	}

}