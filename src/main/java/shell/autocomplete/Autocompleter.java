package shell.autocomplete;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.SequencedSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import shell.Main;
import shell.Shell;
import shell.autocomplete.impl.BuiltinCompletionResolver;
import shell.autocomplete.impl.ExecutableCompletionResolver;
import shell.autocomplete.impl.FileCompletionResolver;

public class Autocompleter {

	public static final Comparator<String> SHORTEST_FIRST = Comparator.comparingInt(String::length).thenComparing(String::compareTo);

	@Getter
	public final List<CompletionResolver> resolvers = List.of(
		BuiltinCompletionResolver.INSTANCE,
		ExecutableCompletionResolver.INSTANCE,
		FileCompletionResolver.INSTANCE
	);

	public Result autocomplete(Shell shell, StringBuilder line, boolean bellRang) {
		final var currentLine = line.toString();

		final var lastSpaceIndex = currentLine.lastIndexOf(' ');
		final var isBeginningCommand = lastSpaceIndex == -1;
		final var beginning = isBeginningCommand
			? currentLine
			: currentLine.substring(lastSpaceIndex + 1, currentLine.length());

		final String prefix;
		final Path parent;
		if (beginning.isBlank()) {
			parent = null;
			prefix = "";
		} else if (beginning.endsWith("/")) {
			parent = Path.of(beginning);
			prefix = "";
		} else {
			final var beginningPath = Path.of(beginning);
			prefix = beginningPath.getFileName().toString();
			parent = beginningPath.getParent();
		}

		final var directory = parent != null
			? shell.getWorkingDirectory().resolve(parent)
			: shell.getWorkingDirectory();

		final var candidates = resolvers.stream()
			.map((resolver) -> resolver.getCompletions(shell, isBeginningCommand, directory, prefix))
			.flatMap(Set::stream)
			.map((candidate) -> candidate.substring(prefix.length()))
			.filter(Predicate.not(String::isBlank))
			.collect(Collectors.toCollection(() -> new TreeSet<>(SHORTEST_FIRST)));

		if (candidates.isEmpty()) {
			return Result.NONE;
		}

		if (candidates.size() == 1) {
			final var candidate = candidates.first();

			writeCandidate(line, candidate, false);

			return Result.FOUND;
		}

		final var sharedPrefix = findSharedPrefix(candidates);
		if (!sharedPrefix.isEmpty()) {
			writeCandidate(line, sharedPrefix, true);

			return Result.MORE;
		}

		if (bellRang) {
			System.out.print(
				candidates.stream()
					.sorted()
					.map(beginning::concat)
					.collect(Collectors.joining("  ", "\n", "\n"))
			);

			Main.prompt();

			System.out.print(line);
			System.out.flush();
		}

		return Result.MORE;
	}

	static String findSharedPrefix(SequencedSet<String> candidates) {
		final var first = candidates.getFirst();
		if (first.isEmpty()) {
			return "";
		}

		final var firstLength = first.length();

		var end = 0;
		while (end <= firstLength) {
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

			++end;
		}

		return first.substring(0, end);
	}

	private void writeCandidate(StringBuilder line, String candidate, boolean hasMore) {
		line.append(candidate);
		System.out.print(candidate);

		if (!hasMore && !candidate.endsWith("/")) {
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