package shell.job;

import java.util.ArrayList;
import java.util.List;

public class JobManager {

	private final List<Entry> entries = new ArrayList<>();

	public void add(Process process, List<String> command) {
		final var number = getNextNumber();
		entries.add(new Entry(number, process, command));

		System.err.printf("[%s] %s%n", number, process.pid());
	}

	public void dump() {
		for (final var entry : entries) {
			final var status = "Running";
			System.err.printf("[%s]+  %-24s %s &%n", entry.number(), status, entry.command());
		}
	}

	public int getNextNumber() {
		return entries.stream()
			.mapToInt(Entry::number)
			.max()
			.orElse(0) + 1;
	}

	record Entry(
		int number,
		Process process,
		String command
	)

	{

		Entry(
			int number,
			Process process,
			List<String> command
		) {
			this(number, process, String.join(" ", command));
		}

	}

}