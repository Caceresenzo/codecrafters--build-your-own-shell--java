package shell.terminal;

import java.util.LinkedHashMap;

import lombok.SneakyThrows;

public class Terminal implements AutoCloseable {

	private final LibC.struct_termios previous = new LibC.struct_termios();

	@SneakyThrows
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
		//		termios.c_iflag &= ~(LibC.IGNCR);

		final var c_iflag = new LinkedHashMap<String, Integer>();
		c_iflag.put("IUCLC", 0x0200);
		c_iflag.put("IXON", 0x0400);
		c_iflag.put("IXOFF", 0x1000);
		c_iflag.put("IMAXBEL", 0x2000);
		c_iflag.put("IUTF8", 0x4000);

		System.out.printf("c_iflag (%d): ", termios.c_iflag);
		for (final var entry : c_iflag.entrySet()) {
			if ((entry.getValue() & termios.c_iflag) != 0) {
				System.out.printf("%s  ", entry.getKey());
			}
		}
		System.out.println();

		final var c_lflags = new LinkedHashMap<String, Integer>();
		c_lflags.put("ISIG", 0x00001);
		c_lflags.put("ICANON", 0x00002);
		c_lflags.put("XCASE", 0x00004);
		c_lflags.put("ECHO", 0x00008);
		c_lflags.put("ECHOE", 0x00010);
		c_lflags.put("ECHOK", 0x00020);
		c_lflags.put("ECHONL", 0x00040);
		c_lflags.put("NOFLSH", 0x00080);
		c_lflags.put("TOSTOP", 0x00100);
		c_lflags.put("ECHOCTL", 0x00200);
		c_lflags.put("ECHOPRT", 0x00400);
		c_lflags.put("ECHOKE", 0x00800);
		c_lflags.put("FLUSHO", 0x01000);
		c_lflags.put("PENDIN", 0x04000);
		c_lflags.put("IEXTEN", 0x08000);
		c_lflags.put("EXTPROC", 0x10000);

		System.out.printf("c_lflags (%d): ", termios.c_lflag);
		for (final var entry : c_lflags.entrySet()) {
			if ((entry.getValue() & termios.c_lflag) != 0) {
				System.out.printf("%s  ", entry.getKey());
			}
		}
		System.out.println();

		termios.c_iflag = 25862;
		termios.c_lflag = 2619;
//		termios.c_lflag &= ~(LibC.ECHO | LibC.ICANON);

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
		final var value = System.in.read();
		if (value == -1) {
			return 0;
		}

		return (char) value;
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
		//		System.out.flush();
		//		final var bytes = string.getBytes();
		//		JTermios.write(STDOUT_FD, bytes, bytes.length);
	}

}