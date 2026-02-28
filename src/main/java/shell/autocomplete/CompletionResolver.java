package shell.autocomplete;

import java.nio.file.Path;
import java.util.Set;

import shell.Shell;

@FunctionalInterface
public interface CompletionResolver {

	Set<String> getCompletions(Shell shell, boolean isCommand, Path directory, String prefix);

}