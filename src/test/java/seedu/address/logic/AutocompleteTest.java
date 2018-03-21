package seedu.address.logic;

import org.junit.Before;
import org.junit.Test;

import seedu.address.logic.commands.CommandTrie;

public class AutocompleteTest {

    private CommandTrie commandTrie;

    @Before
    public void setup() {
        commandTrie = new CommandTrie();
    }

    @Test
    public void autocomplete_unique_prefix() {
        assert commandTrie.attemptAutoComplete("a").equals("add");
        assert commandTrie.attemptAutoComplete("he").equals("help");
    }

    @Test
    public void autocomplete_multiple_prefix() {
        assert commandTrie.attemptAutoComplete("e").equals("e");
        assert commandTrie.attemptAutoComplete("H").equals("H");

    }
}
