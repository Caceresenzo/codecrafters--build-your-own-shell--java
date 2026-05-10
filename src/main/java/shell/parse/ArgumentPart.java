package shell.parse;

public sealed interface ArgumentPart {

	public record Literal(String value) implements ArgumentPart {}

	public record Variable(String name) implements ArgumentPart {}

}