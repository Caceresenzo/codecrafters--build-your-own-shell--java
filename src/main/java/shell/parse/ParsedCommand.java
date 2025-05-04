package shell.parse;

import java.util.List;

public record ParsedCommand(
	List<String> arguments,
	List<Redirect> redirects
) {

	public String program() {
		return arguments.getFirst();
	}

}