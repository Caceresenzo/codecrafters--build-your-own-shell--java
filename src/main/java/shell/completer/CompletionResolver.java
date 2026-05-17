package shell.completer;

import java.nio.file.Path;
import java.util.Set;

import shell.Shell;

@FunctionalInterface
public interface CompletionResolver {

	Set<String> getCompletions(Shell shell, String line, Path directory, String prefix);

}