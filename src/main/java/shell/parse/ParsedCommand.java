package shell.parse;

import java.util.List;

public record ParsedCommand(
	List<String> arguments,
	List<Redirect> redirects,
	boolean isJob
) {

	public String program() {
		return arguments.getFirst();
	}

}