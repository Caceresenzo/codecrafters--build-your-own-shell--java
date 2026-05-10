package shell.parse;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import shell.io.StandardNamedStream;

public class LineParser {

	public static final char SPACE = ' ';
	public static final char SINGLE = '\'';
	public static final char DOUBLE = '"';
	public static final char BACKSLASH = '\\';
	public static final char GREATER_THAN = '>';
	public static final char PIPE = '|';
	public static final char DOLLAR = '$';
	public static final char AMPERSAND = '&';
	public static final char BRACE_OPEN = '{';
	public static final char BRACE_CLOSE = '}';

	private final CharacterIterator iterator;

	private final List<ParsedCommand> commands = new ArrayList<>();

	private List<Argument> arguments = new ArrayList<>();
	private List<ArgumentPart> argumentParts = new ArrayList<>();
	private List<Redirect> redirects = new ArrayList<>();
	private boolean isJob;

	public LineParser(String line) {
		this.iterator = new StringCharacterIterator(line);
	}

	public List<ParsedCommand> parse() {
		Argument argument;

		iterator.first();
		while ((argument = nextArgument()) != null) {
			arguments.add(argument);
		}

		if (!arguments.isEmpty()) {
			pipe();
		}

		return commands;
	}

	private void appendLiteralPart(StringBuilder stringBuilder) {
		if (stringBuilder.isEmpty()) {
			return;
		}

		argumentParts.add(new ArgumentPart.Literal(stringBuilder.toString()));
		stringBuilder.setLength(0);
	}

	private Argument nextArgument() {
		final var stringBuilder = new StringBuilder();

		for (var character = iterator.current(); character != CharacterIterator.DONE; character = iterator.next()) {
			switch (character) {
				case SPACE -> {
					appendLiteralPart(stringBuilder);
					if (!argumentParts.isEmpty()) {
						return toArgument(stringBuilder);
					}
				}
				case SINGLE -> singleQuote(stringBuilder);
				case DOUBLE -> doubleQuote(stringBuilder);
				case BACKSLASH -> backslash(stringBuilder, false);
				case GREATER_THAN -> redirect(StandardNamedStream.OUTPUT);
				case PIPE -> pipe();
				case DOLLAR -> {
					if (!stringBuilder.isEmpty()) {
						argumentParts.add(new ArgumentPart.Literal(stringBuilder.toString()));
						stringBuilder.setLength(0);
					}

					variable();
				}
				case AMPERSAND -> {
					isJob = true;
				}
				default -> {
					if (Character.isDigit(character) && peek() == GREATER_THAN) {
						iterator.next();
						redirect(StandardNamedStream.fromFileDescriptor(Character.digit(character, 10)));
						continue;
					}

					stringBuilder.append(character);
				}
			}
		}

		appendLiteralPart(stringBuilder);
		if (!argumentParts.isEmpty()) {
			return toArgument(stringBuilder);
		}

		return null;
	}

	private Argument toArgument(StringBuilder lastLiteralPart) {
		if (lastLiteralPart != null && !lastLiteralPart.isEmpty()) {
			argumentParts.add(new ArgumentPart.Literal(lastLiteralPart.toString()));
		}

		final var argument = new Argument(List.copyOf(argumentParts));
		argumentParts.clear();

		return argument;
	}

	private void singleQuote(StringBuilder stringBuilder) {
		char character;
		while ((character = iterator.next()) != CharacterIterator.DONE && character != SINGLE) {
			stringBuilder.append(character);
		}
	}

	private void doubleQuote(StringBuilder stringBuilder) {
		char character;
		while ((character = iterator.next()) != CharacterIterator.DONE && character != DOUBLE) {
			switch (character) {
				case BACKSLASH -> backslash(stringBuilder, true);
				default -> stringBuilder.append(character);
			}
		}
	}

	private void backslash(StringBuilder stringBuilder, boolean inQuote) {
		var character = iterator.next();
		if (character == CharacterIterator.DONE) {
			return;
		}

		if (inQuote) {
			final var mappedCharacter = mapBackslashCharacter(character);

			if (mappedCharacter != CharacterIterator.DONE) {
				character = mappedCharacter;
			} else {
				stringBuilder.append(BACKSLASH);
			}
		}

		stringBuilder.append(character);
	}

	private char mapBackslashCharacter(char character) {
		return switch (character) {
			case DOUBLE -> DOUBLE;
			case BACKSLASH -> BACKSLASH;
			default -> CharacterIterator.DONE;
		};
	}

	private void redirect(StandardNamedStream standardStream) {
		var character = iterator.next();
		if (character == CharacterIterator.DONE) {
			return;
		}

		final var append = character == GREATER_THAN;
		if (append) {
			iterator.next();
		}

		final var path = nextArgument();

		redirects.add(new Redirect(
			standardStream,
			path,
			append
		));
	}

	private void pipe() {
		if (!argumentParts.isEmpty()) {
			arguments.add(toArgument(null));
		}

		commands.add(new ParsedCommand(arguments, redirects, isJob));

		arguments = new ArrayList<>();
		argumentParts = new ArrayList<>();
		redirects = new ArrayList<>();
		isJob = false;
	}

	private void variable() {
		final var nameNameBuilder = new StringBuilder();

		char character;
		if (peek() == BRACE_OPEN) {
			iterator.next();

			while ((character = peek()) != CharacterIterator.DONE && character != BRACE_CLOSE) {
				nameNameBuilder.append(character);
				iterator.next();
			}

			if (peek() == BRACE_CLOSE) {
				iterator.next();
			}
		} else {
			while ((character = peek()) != CharacterIterator.DONE && (Character.isAlphabetic(character) || Character.isDigit(character) || character == '_')) {
				nameNameBuilder.append(character);
				iterator.next();
			}
		}

		final var name = nameNameBuilder.toString();
		argumentParts.add(new ArgumentPart.Variable(name));
	}

	public char peek() {
		final var character = iterator.next();
		if (character != CharacterIterator.DONE) {
			iterator.previous();
		}

		return character;
	}

}