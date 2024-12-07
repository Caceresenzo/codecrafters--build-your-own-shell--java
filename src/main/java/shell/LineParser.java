package shell;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

public class LineParser {

	public static final char SPACE = ' ';
	public static final char SINGLE = '\'';

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

}