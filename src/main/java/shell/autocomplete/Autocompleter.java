package shell.autocomplete;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Getter;
import shell.Shell;
import shell.autocomplete.impl.BuiltinCompletionResolver;

public class Autocompleter {

	public static final Comparator<String> SHORTEST_FIRST = Comparator.comparingInt(String::length).thenComparing(String::compareTo);

	@Getter
	public final List<CompletionResolver> resolvers = List.of(
		BuiltinCompletionResolver.INSTANCE
	);

	public Result autocomplete(Shell shell, StringBuilder line) {
		final var beginning = line.toString();

		final var candidates = resolvers.stream()
			.map((resolver) -> resolver.getCompletions(shell, beginning))
			.flatMap(Set::stream)
			.map((candidate) -> candidate.substring(beginning.length()))
			.collect(Collectors.toCollection(() -> new TreeSet<>(SHORTEST_FIRST)));

		if (candidates.isEmpty()) {
			return Result.NONE;
		}

		if (candidates.size() == 1) {
			final var candidate = candidates.first();

			writeCandidate(line, candidate);

			return Result.FOUND;
		}

		return Result.MORE;
	}

	private void writeCandidate(StringBuilder line, String candidate) {
		line.append(candidate);
		System.out.print(candidate);

		line.append(' ');
		System.out.print(' ');
	}

	public enum Result {

		NONE,
		FOUND,
		MORE;

	}

}