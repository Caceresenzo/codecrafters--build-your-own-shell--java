package shell;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

public class LineParser {

	public static final char SPACE = ' ';
	public static final char SINGLE = '\'';
	public static final char DOUBLE = '"';
	public static final char BACKSLASH = '\\';

	private final CharacterIterator iterator;
	private final StringBuilder stringBuilder;

	public LineParser(String line) {
		this.iterator = new StringCharacterIterator(line);
		this.stringBuilder = new StringBuilder(line.length());
	}

	public String[] parse() {
		final var strings = new ArrayList<String>();

		for (char character = iterator.first(); character != CharacterIterator.DONE; character = iterator.next()) {
			switch (character) {
				case SPACE -> {
					if (!stringBuilder.isEmpty()) {
						strings.add(stringBuilder.toString());
						stringBuilder.setLength(0);
					}
				}
				case SINGLE -> singleQuote();
				case DOUBLE -> doubleQuote();
				case BACKSLASH -> backslash(false);
				default -> stringBuilder.append(character);
			}
		}

		if (!stringBuilder.isEmpty()) {
			strings.add(stringBuilder.toString());
		}

		return strings.toArray(String[]::new);
	}

	private void singleQuote() {
		char character;
		while ((character = iterator.next()) != CharacterIterator.DONE && character != SINGLE) {
			stringBuilder.append(character);
		}
	}

	private void doubleQuote() {
		char character;
		while ((character = iterator.next()) != CharacterIterator.DONE && character != DOUBLE) {
			switch (character) {
				case BACKSLASH -> backslash(true);
				default -> stringBuilder.append(character);
			}
		}
	}

	private void backslash(boolean inQuote) {
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

}