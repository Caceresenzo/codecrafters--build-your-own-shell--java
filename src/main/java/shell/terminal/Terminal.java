package shell.terminal;

import lombok.SneakyThrows;

public class Terminal implements AutoCloseable {

	private final LibC.struct_termios previous = new LibC.struct_termios();

	public Terminal() {
		checkErrno(LibC.INSTANCE.tcgetattr(LibC.STDIN_FILENO, previous));

		//		System.out.println(previous);
		LibC.struct_termios termios = previous.clone();

		//		termios.c_iflag = 9478;
		//		termios.c_oflag = 5;
		//		termios.c_cflag = 191;
		//		termios.c_cflag = 35387;

		//		termios.c_lflag &= ~(LibC.ECHO | LibC.ICANON);
//		termios.c_lflag &= ~(LibC.ICANON);
		termios.c_iflag &= ~(LibC.IGNCR);

				termios.c_lflag &= ~(LibC.ICANON | LibC.IEXTEN | LibC.ISIG);
//				termios.c_lflag &= ~(LibC.ECHO | LibC.ICANON | LibC.IEXTEN | LibC.ISIG);
		//		termios.c_iflag &= ~(LibC.IXON | LibC.IGNCR);
		//		termios.c_iflag &= ~(LibC.OPOST);

		//		System.out.println(termios);
		//		termios.c_iflag &= ~(JTermios.IXON);
		//		termios.c_oflag &= ~(JTermios.OPOST);

		termios.c_cc[LibC.VMIN] = 0;
		termios.c_cc[LibC.VTIME] = 1;

		checkErrno(LibC.INSTANCE.tcsetattr(LibC.STDIN_FILENO, LibC.TCSANOW, termios));
	}

	@SneakyThrows
	public char read() {
		//		JTermios.read(STDIN_FD, buffer, buffer.length);

		//		final var value = buffer[0];
		//		return (char) value;

		return (char) System.in.read();
	}

	@Override
	public void close() {
		//		checkErrno(JTermios.tcsetattr(STDIN_FD, JTermios.TCSANOW, previous));

		checkErrno(LibC.INSTANCE.tcsetattr(LibC.STDIN_FILENO, LibC.TCSANOW, previous));
	}

	private static void checkErrno(int previousReturn) {
		if (previousReturn != -1) {
			return;
		}

		final var errno = com.sun.jna.Native.getLastError();
		throw new IllegalStateException("errno: %s: %s".formatted(errno, LibC.INSTANCE.strerror(errno)));
	}

	public static void write(String string) {
		System.out.print(string);
		System.out.flush();
		//		final var bytes = string.getBytes();
		//		JTermios.write(STDOUT_FD, bytes, bytes.length);
	}

}