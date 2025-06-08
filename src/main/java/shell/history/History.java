package shell.history;

import java.util.ArrayList;
import java.util.List;

public class History {

	private final List<String> lines = new ArrayList<>();

	public int size() {
		return lines.size();
	}

	public boolean add(String e) {
		return lines.add(e);
	}

	public String get(int index) {
		return lines.get(index);
	}

}