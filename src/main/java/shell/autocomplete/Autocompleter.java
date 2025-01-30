package shell.autocomplete;

import java.util.Comparator;
import java.util.List;
import java.util.SequencedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Getter;
import shell.Main;
import shell.Shell;
import shell.autocomplete.impl.BuiltinCompletionResolver;
import shell.autocomplete.impl.ExecutableCompletionResolver;

public class Autocompleter {

	public static final Comparator<String> SHORTEST_FIRST = Comparator.comparingInt(String::length).thenComparing(String::compareTo);

	@Getter
	public final List<CompletionResolver> resolvers = List.of(
		BuiltinCompletionResolver.INSTANCE,
		ExecutableCompletionResolver.INSTANCE
	);

	public Result autocomplete(Shell shell, StringBuilder line, boolean bellRang) {
		final var beginning = line.toString();
		if (beginning.isBlank()) {
			return Result.FOUND;
		}

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

			writeCandidate(line, candidate, false);

			return Result.FOUND;
		}

		final var prefix = findSharedPrefix(candidates);
		if (!prefix.isEmpty()) {
			writeCandidate(line, prefix, true);

			return Result.MORE;
		}

		if (bellRang) {
			System.out.print(
				candidates.stream()
					.map(beginning::concat)
					.collect(Collectors.joining("  ", "\n", "\n"))
			);

			Main.prompt();

			System.out.print(beginning);
			System.out.flush();
		}

		return Result.MORE;
	}

	private static String findSharedPrefix(SequencedSet<String> candidates) {
		final var first = candidates.getFirst();
		if (first.isEmpty()) {
			return "";
		}

		var end = 0;
		for (; end < first.length(); ++end) {
			var oneIsNotMatching = false;

			final var iterator = candidates.iterator();
			iterator.next(); /* skip first */

			while (iterator.hasNext()) {
				final var candidate = iterator.next();

				if (!first.subSequence(0, end).equals(candidate.subSequence(0, end))) {
					oneIsNotMatching = true;
					break;
				}
			}

			if (oneIsNotMatching) {
				end -= 1;
				break;
			}
		}

		return first.substring(0, end);
	}

	private void writeCandidate(StringBuilder line, String candidate, boolean hasMore) {
		line.append(candidate);
		System.out.print(candidate);

		if (!hasMore) {
			line.append(' ');
			System.out.print(' ');
		}
	}

	public enum Result {

		NONE,
		FOUND,
		MORE;

	}

}