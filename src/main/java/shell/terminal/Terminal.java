package shell.terminal;

import jtermios.JTermios;
import jtermios.Termios;
import lombok.SneakyThrows;

public class Terminal implements AutoCloseable {

	private static final int STDIN_FD = 0;
	private static final int STDOUT_FD = 1;

	private final Termios previous = new Termios();
	private final byte[] buffer = { 0 };

	public Terminal() {
		checkErrno(JTermios.tcgetattr(STDIN_FD, previous));

		Termios termios = new Termios();
		termios.set(previous);

		termios.c_lflag &= ~(JTermios.ECHO | JTermios.ICANON);
		termios.c_iflag &= ~(JTermios.IGNCR);
//		termios.c_iflag &= ~(JTermios.IXON);
//		termios.c_oflag &= ~(JTermios.OPOST);

		termios.c_cc[JTermios.VMIN] = 1;
		termios.c_cc[JTermios.VTIME] = 0;
		checkErrno(JTermios.tcsetattr(STDIN_FD, JTermios.TCSANOW, termios));
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
		checkErrno(JTermios.tcsetattr(STDIN_FD, JTermios.TCSANOW, previous));
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