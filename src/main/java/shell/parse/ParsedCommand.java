package shell.parse;

import java.util.List;
import java.util.Objects;

import shell.Environment;

public record ParsedCommand(
	List<Argument> arguments,
	List<Redirect> redirects,
	boolean isJob
) {

	public List<String> resolveArguments(Environment environment) {
		return arguments.stream()
			.map((argument) -> argument.resolve(environment))
			.filter(Objects::nonNull)
			.toList();
	}

}