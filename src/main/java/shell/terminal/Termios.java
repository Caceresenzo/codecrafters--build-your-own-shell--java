package shell.terminal;

import lombok.SneakyThrows;

public class Termios implements AutoCloseable {

	private final LibC.struct_termios previous = new LibC.struct_termios();

	@SneakyThrows
	public Termios() {
		checkErrno(LibC.INSTANCE.tcgetattr(LibC.STDIN_FILENO, previous));

		LibC.struct_termios termios = previous.clone();

		termios.c_lflag &= ~(LibC.ECHO | LibC.ICANON);
		termios.c_cc[LibC.VMIN] = 0;
		termios.c_cc[LibC.VTIME] = 1;

		checkErrno(LibC.INSTANCE.tcsetattr(LibC.STDIN_FILENO, LibC.TCSANOW, termios));
	}

	@Override
	public void close() {
		checkErrno(LibC.INSTANCE.tcsetattr(LibC.STDIN_FILENO, LibC.TCSANOW, previous));
	}

	private static void checkErrno(int previousReturn) {
		if (previousReturn != -1) {
			return;
		}

		final var errno = com.sun.jna.Native.getLastError();
		throw new IllegalStateException("errno: %s: %s".formatted(errno, LibC.INSTANCE.strerror(errno)));
	}

	public static Termios enableRawMode() {
		return new Termios();
	}

}