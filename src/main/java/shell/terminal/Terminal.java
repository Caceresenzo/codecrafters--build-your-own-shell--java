package shell.terminal;

import jtermios.JTermios;
import lombok.SneakyThrows;

public class Terminal implements AutoCloseable {

	private final LibC.struct_termios previous = new LibC.struct_termios();

	public Terminal() {
		checkErrno(LibC.INSTANCE.tcgetattr(LibC.STDIN_FILENO, previous));

		System.out.println(previous);
		LibC.struct_termios termios = previous.clone();
		System.out.println(termios);

		termios.c_lflag &= ~(LibC.ECHO | LibC.ICANON);
		termios.c_iflag &= ~(LibC.IGNCR);
		//		termios.c_iflag &= ~(JTermios.IXON);
		//		termios.c_oflag &= ~(JTermios.OPOST);

		termios.c_cc[LibC.VMIN] = 1;
		termios.c_cc[LibC.VTIME] = 0;

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

		final var errno = JTermios.errno();
		throw new IllegalStateException("errno: %s".formatted(errno));
	}

	public static void write(String string) {
		System.out.print(string);
		System.out.flush();
		//		final var bytes = string.getBytes();
		//		JTermios.write(STDOUT_FD, bytes, bytes.length);
	}

}