import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.print("$ ");

		try (Scanner scanner = new Scanner(System.in)) {
			final var input = scanner.nextLine();
			final var arguments = input.split(" ");
			final var program = arguments[0];

			System.out.println("%s: command not found".formatted(program));
		}
	}

}
