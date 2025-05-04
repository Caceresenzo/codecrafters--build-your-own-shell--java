package shell;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import shell.io.StandardNamedStream;
import shell.parse.LineParser;
import shell.parse.ParsedCommand;
import shell.parse.Redirect;

class LineParserTest {

	@Test
	void testBackslash() {
		assertThat(parseArguments("echo \"before\\   after\""))
			.containsExactly("echo", "before\\   after");

		assertThat(parseArguments("echo shell\\ \\ \\ \\ \\ \\ hello"))
			.containsExactly("echo", "shell      hello");

		assertThat(parseArguments("echo example\\nscript"))
			.containsExactly("echo", "examplenscript");

		assertThat(parseArguments("echo \"/tmp/foo/f\\n40\""))
			.containsExactly("echo", "/tmp/foo/f\\n40");

		assertThat(parseArguments("echo \"/tmp/foo/f\\67\""))
			.containsExactly("echo", "/tmp/foo/f\\67");

		assertThat(parseArguments("echo \"/tmp/foo/f'\\'47\""))
			.containsExactly("echo", "/tmp/foo/f'\\'47");

		assertThat(parseArguments("echo \"hello'script'\\\\n'world\""))
			.containsExactly("echo", "hello'script'\\n'world");

		assertThat(parseArguments("echo \"hello\\\"insidequotes\"script\\\""))
			.containsExactly("echo", "hello\"insidequotesscript\"");
	}

	@Test
	void testRedirect() {
		assertThat(parse("echo a > out.txt").redirects())
			.containsExactly(new Redirect(StandardNamedStream.OUTPUT, Path.of("out.txt"), false));

		assertThat(parse("echo a >> out.txt").redirects())
			.containsExactly(new Redirect(StandardNamedStream.OUTPUT, Path.of("out.txt"), true));

		assertThat(parse("echo a >> out.txt > out2.txt"))
			.isEqualTo(new ParsedCommand(
				List.of("echo", "a"),
				List.of(
					new Redirect(StandardNamedStream.OUTPUT, Path.of("out.txt"), true),
					new Redirect(StandardNamedStream.OUTPUT, Path.of("out2.txt"), false)
				)
			));
	}

	@Test
	void testRedirectToDifferentStreams() {
		assertThat(parse("echo a 2> out.txt").redirects())
			.containsExactly(new Redirect(StandardNamedStream.ERROR, Path.of("out.txt"), false));
	}

	ParsedCommand parse(String line) {
		return new LineParser(line).parse().getFirst();
	}

	List<String> parseArguments(String line) {
		return new LineParser(line).parse().getFirst().arguments();
	}

}