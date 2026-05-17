package shell.completer;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import lombok.Getter;
import shell.Main;
import shell.Shell;
import shell.completer.impl.BuiltinCompletionResolver;
import shell.completer.impl.CustomCompletionResolver;
import shell.completer.impl.ExecutableCompletionResolver;
import shell.completer.impl.FileCompletionResolver;

public class Completer {

	public static final Comparator<String> SHORTEST_FIRST = Comparator.comparingInt(String::length).thenComparing(String::compareTo);

	@Getter
	private final CustomCompletionResolver customCompletionResolver = new CustomCompletionResolver();

	@Getter
	public final List<CompletionResolver> resolvers = List.of(
		BuiltinCompletionResolver.INSTANCE,
		ExecutableCompletionResolver.INSTANCE,
		FileCompletionResolver.INSTANCE,
		customCompletionResolver
	);

	public Result complete(Shell shell, StringBuilder line, boolean bellRang) {
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

		final var rawCandidates = new HashSet<String>();

		if (isBeginningCommand) {
			rawCandidates.addAll(BuiltinCompletionResolver.INSTANCE.getCompletions(shell, currentLine, directory, prefix));
			rawCandidates.addAll(ExecutableCompletionResolver.INSTANCE.getCompletions(shell, currentLine, directory, prefix));
		}

		final var customCandidates = customCompletionResolver.getCompletions(shell, currentLine, directory, prefix);
		if (!customCandidates.isEmpty()) {
			rawCandidates.addAll(customCandidates);
		} else {
			rawCandidates.addAll(FileCompletionResolver.INSTANCE.getCompletions(shell, currentLine, directory, prefix));
		}

		final var candidates = rawCandidates.stream()
			.map((candidate) -> candidate.substring(prefix.length()))
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
		while (end < firstLength) {
			++end;

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