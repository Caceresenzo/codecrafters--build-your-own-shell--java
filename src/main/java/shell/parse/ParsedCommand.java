package shell.parse;

import java.util.List;

import shell.Environment;

public record ParsedCommand(
	List<Argument> arguments,
	List<Redirect> redirects,
	boolean isJob
) {

	public List<String> resolveArguments(Environment environment) {
		return arguments.stream()
			.map((argument) -> argument.resolve(environment))
			.toList();
	}

}