package shell.command;

import java.util.List;
import java.util.OptionalInt;

import shell.Shell;
import shell.io.RedirectStreams;

public interface Executable {

	OptionalInt execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams);

}