package shell;

import java.util.NoSuchElementException;
import java.util.Scanner;

import lombok.SneakyThrows;
import shell.io.RedirectStreams;
import shell.parse.LineParser;

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

	@SneakyThrows
	public static void eval(Shell shell, String line) {
		final var parsedLine = new LineParser(line).parse();

		final var arguments = parsedLine.arguments();
		final var program = arguments.getFirst();

		final var command = shell.find(program);
		if (command != null) {
			try (final var redirectStreams = RedirectStreams.from(parsedLine.redirects())) {
				command.execute(shell, arguments, redirectStreams);
			}
		} else {
			System.out.println("%s: command not found".formatted(program));
		}
	}

}
