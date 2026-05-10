package shell;

import java.util.HashMap;
import java.util.Map;

public class Environment {

	private final Map<String, String> variables = new HashMap<>();

	public void setVariable(String name, String value) {
		variables.put(name, value);
	}

	public String getVariable(String name) {
		return variables.get(name);
	}

	public static boolean isValidName(String name) {
		if (name.isEmpty()) {
			return false;
		}

		final var first = name.charAt(0);
		if (!Character.isLetter(first) && first != '_') {
			return false;
		}

		for (final var character : name.toCharArray()) {
			if (!Character.isLetterOrDigit(character) && character != '_') {
				return false;
			}
		}

		return true;
	}

}