package shell.history;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class History {

	private final List<String> previousLines = new ArrayList<>();

	public int size() {
		return previousLines.size();
	}

	public boolean add(String e) {
		return previousLines.add(e);
	}

	public String get(int index) {
		return previousLines.get(index);
	}

	@SneakyThrows
	public void readFrom(Path path) {
		List<String> lines;
		try {
			lines = Files.readAllLines(path);
		} catch (FileNotFoundException __) {
			return;
		}

		for (final var line : lines) {
			previousLines.add(line);
		}
	}

	@SneakyThrows
	public void writeTo(Path path) {
		Files.write(path, previousLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

}