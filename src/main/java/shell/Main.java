package shell;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		Shell shell = new Shell();

		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				final var line = read(scanner);
				eval(shell, line);
			}
		} catch (NoSuchElementException exception) {}
	}

	public static String read(Scanner scanner) {
		while (true) {
			System.out.print("$ ");
			final var line = scanner.nextLine();;

			if (!line.isBlank()) {
				return line;
			}
		}
	}

	public static void eval(Shell shell, String line) {
		final var arguments = new LineParser(line).parse();
		final var program = arguments[0];

		final var command = shell.find(program);
		if (command != null) {
			command.execute(shell, arguments);
		} else {
			System.out.println("%s: command not found".formatted(program));
		}
	}

}
