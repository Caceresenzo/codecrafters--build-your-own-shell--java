package shell;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LineParserTest {

	@Test
	void testBackslash() {
		assertThat(parse("echo \"before\\   after\""))
			.containsExactly("echo", "before\\   after");

		assertThat(parse("echo shell\\ \\ \\ \\ \\ \\ hello"))
			.containsExactly("echo", "shell      hello");

		assertThat(parse("echo example\\nscript"))
			.containsExactly("echo", "examplenscript");

		assertThat(parse("echo \"/tmp/foo/f\\n40\""))
			.containsExactly("echo", "/tmp/foo/f\\n40");

		assertThat(parse("echo \"/tmp/foo/f\\67\""))
			.containsExactly("echo", "/tmp/foo/f\\67");

		assertThat(parse("echo \"/tmp/foo/f'\\'47\""))
			.containsExactly("echo", "/tmp/foo/f'\\'47");

		assertThat(parse("echo \"hello'script'\\\\n'world\""))
			.containsExactly("echo", "hello'script'\\n'world");

		assertThat(parse("echo \"hello\\\"insidequotes\"script\\\""))
			.containsExactly("echo", "hello\"insidequotesscript\"");
	}

	String[] parse(String line) {
		return new LineParser(line).parse();
	}

}