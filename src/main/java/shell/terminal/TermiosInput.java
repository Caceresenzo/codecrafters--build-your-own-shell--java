package shell.terminal;

import jtermios.JTermios;
import jtermios.Termios;
import lombok.SneakyThrows;

public class TermiosInput implements AutoCloseable {

	private static final int STDIN_FD = 0;

	private final Termios previous = new Termios();
	private final Termios new_ = new Termios();

	public TermiosInput() {
		JTermios.tcgetattr(STDIN_FD, previous);

		Termios termios = new Termios();
		JTermios.tcgetattr(STDIN_FD, termios);
		termios.c_iflag &= JTermios.IGNCR;
		termios.c_lflag ^= JTermios.ICANON;
		termios.c_lflag ^= JTermios.ECHO;
		termios.c_cc[JTermios.VMIN] = 1;
		termios.c_cc[JTermios.VTIME] = 0;
		JTermios.tcsetattr(STDIN_FD, JTermios.TCSANOW, termios);
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
		JTermios.tcsetattr(STDIN_FD, JTermios.TCSANOW, previous);
	}

}