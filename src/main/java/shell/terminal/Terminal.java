package shell.terminal;

import jtermios.JTermios;
import jtermios.Termios;
import lombok.SneakyThrows;

public class Terminal implements AutoCloseable {

	private static final int STDIN_FD = 0;
	private static final int STDOUT_FD = 1;

	private final Termios previous = new Termios();

	public Terminal() {
		checkErrno(JTermios.tcgetattr(STDIN_FD, previous));

		Termios termios = new Termios();
		termios.set(termios);

		checkErrno(JTermios.tcgetattr(STDIN_FD, termios));
		//		termios.c_iflag &= JTermios.IGNCR;
		termios.c_lflag &= ~(JTermios.ECHO | JTermios.ICANON);
		termios.c_cc[JTermios.VMIN] = 1;
		termios.c_cc[JTermios.VTIME] = 0;
		checkErrno(JTermios.tcsetattr(STDIN_FD, JTermios.TCSANOW, termios));
	}

	@SneakyThrows
	public char read() {
		var value = System.in.read();

		if (value < 0) {
			value = 0;
		}

		return (char) value;
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
		final var bytes = string.getBytes();
		JTermios.write(STDOUT_FD, bytes, bytes.length);
	}

}