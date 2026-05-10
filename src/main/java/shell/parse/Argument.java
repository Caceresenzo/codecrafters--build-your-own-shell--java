package shell.parse;

import java.util.Arrays;
import java.util.List;

import shell.Environment;

public record Argument(
	List<? extends ArgumentPart> parts
) {

	public String resolve(Environment environment) {
		final var builder = new StringBuilder();

		for (ArgumentPart part : parts) {
			if (part instanceof ArgumentPart.Literal(final var value)) {
				builder.append(value);
			} else if (part instanceof ArgumentPart.Variable(final var name)) {
				builder.append(environment.getVariableOrDefault(name, ""));
			}
		}

		return builder.toString();
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