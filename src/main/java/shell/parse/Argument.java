package shell.parse;

import java.util.Arrays;
import java.util.List;

import shell.Environment;

public record Argument(
	List<? extends ArgumentPart> parts
) {

	public String resolve(Environment environment) {
		final var builder = new StringBuilder();

		var canBeSkipped = true;

		for (final var part : parts) {
			if (part instanceof ArgumentPart.Literal(final var value)) {
				builder.append(value);
				canBeSkipped = false;
			} else if (part instanceof ArgumentPart.Variable(final var name)) {
				builder.append(environment.getVariableOrDefault(name, ""));
			}
		}

		final var result = builder.toString();
		if (result.isBlank() && canBeSkipped) {
			return null;
		}

		return result;
	}

	public static Argument literal(String value) {
		return new Argument(List.of(new ArgumentPart.Literal(value)));
	}

	public static List<Argument> literal(String... values) {
		return Arrays.stream(values)
			.map(ArgumentPart.Literal::new)
			.map(List::of)
			.map(Argument::new)
			.toList();
	}

}