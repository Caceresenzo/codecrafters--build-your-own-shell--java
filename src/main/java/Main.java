import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				final var line = read(scanner);
				eval(line);
			}
		}
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

	public static void eval(String line) {
		final var arguments = line.split(" ");
		final var program = arguments[0];

		System.out.println("%s: command not found".formatted(program));
	}

}
