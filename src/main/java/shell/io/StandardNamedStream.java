package shell.io;

import java.io.PrintStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StandardNamedStream {

	OUTPUT(System.out),
	ERROR(System.err),
	UNKNOWN(null);

	private final PrintStream printStream;

	public static StandardNamedStream fromFileDescriptor(int fileDescriptor) {
		return switch (fileDescriptor) {
			case 1 -> OUTPUT;
			case 2 -> ERROR;
			default -> UNKNOWN;
		};
	}

}