package shell.autocomplete;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.SequencedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

class AutocompleterTest {

	@Test
	void findSharedPrefix() {
		final SequencedSet<String> candidates = new TreeSet<>(Autocompleter.SHORTEST_FIRST);
		candidates.add("z_owl/");
		candidates.add("z_owl_fox/");
		candidates.add("z_owl_fox_cow.txt");

		final var sharedPrefix = Autocompleter.findSharedPrefix(candidates);
		assertEquals("z_owl", sharedPrefix);
	}

	@Test
	void findSharedPrefix2() {
		final SequencedSet<String> candidates = new TreeSet<>(Autocompleter.SHORTEST_FIRST);
		candidates.add("w_9/");
		candidates.add("w_8.txt");

		final var sharedPrefix = Autocompleter.findSharedPrefix(candidates);
		assertEquals("w_", sharedPrefix);
	}

}