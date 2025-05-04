package shell.parse;

import java.nio.file.Path;
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

	private final CharacterIterator iterator;

	private final List<ParsedCommand> commands = new ArrayList<>();

	private List<String> arguments = new ArrayList<>();
	private List<Redirect> redirects = new ArrayList<>();

	public LineParser(String line) {
		this.iterator = new StringCharacterIterator(line);
	}

	public List<ParsedCommand> parse() {
		String argument;

		iterator.first();
		while ((argument = nextArgument()) != null) {
			arguments.add(argument);
		}

		if (!arguments.isEmpty()) {
			commands.add(new ParsedCommand(arguments, redirects));
		}

		return commands;
	}

	private String nextArgument() {
		final var stringBuilder = new StringBuilder();

		for (var character = iterator.current(); character != CharacterIterator.DONE; character = iterator.next()) {
			switch (character) {
				case SPACE -> {
					if (!stringBuilder.isEmpty()) {
						return stringBuilder.toString();
					}
				}
				case SINGLE -> singleQuote(stringBuilder);
				case DOUBLE -> doubleQuote(stringBuilder);
				case BACKSLASH -> backslash(stringBuilder, false);
				case GREATER_THAN -> redirect(StandardNamedStream.OUTPUT);
				case PIPE -> pipe();
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

		if (!stringBuilder.isEmpty()) {
			return stringBuilder.toString();
		}

		return null;
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
			Path.of(path),
			append
		));
	}

	private void pipe() {
		commands.add(new ParsedCommand(arguments, redirects));

		arguments = new ArrayList<>();
		redirects = new ArrayList<>();
	}

	public char peek() {
		final var character = iterator.next();
		if (character != CharacterIterator.DONE) {
			iterator.previous();
		}

		return character;
	}

}