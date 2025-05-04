package shell.command;

import java.util.List;

import shell.Shell;
import shell.io.RedirectStreams;

public interface Executable {

	void execute(Shell shell, List<String> arguments, RedirectStreams redirectStreams);

}