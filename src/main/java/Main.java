import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.print("$ ");

		try (Scanner scanner = new Scanner(System.in)) {
			String input = scanner.nextLine();
		}
	}

}
