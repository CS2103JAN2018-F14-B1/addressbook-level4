package seedu.address.logic.commands;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Trie of possible commands. Stores all possible commands for the addressbook.
 * Used for autocomplete functionality by returning a possible command via the attemptAutoComplete function.
 */
public class CommandTrie {

    private TrieNode root = null;
    private Set<String> commandSet;

    public CommandTrie() {
        commandSet = Stream.of(
                EditCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
                AddCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD, SelectCommand.COMMAND_WORD, HistoryCommand.COMMAND_WORD,
                ListCommand.COMMAND_WORD, RedoCommand.COMMAND_WORD,  UndoCommand.COMMAND_WORD
        ).collect(Collectors.toSet());

        for (String command : commandSet) {
            this.insert(command);
        }
    }

    /**
     * Inserts input into Trie
     *
     * @param input command string
     */
    private void insert(String input) {
        char[] inputArray = input.toCharArray();

        if (root == null) {
            root = new TrieNode(inputArray[0], null, null);
            TrieNode temp = root;
            for (int i = 1; i < inputArray.length; i++) {
                temp.setChild(new TrieNode(inputArray[i], null, null));
                temp = temp.getChild();
            }
        } else {
            TrieNode temp = root;
            int i = 0;
            while (temp.hasSibling()) {
                if (temp.getKey() == inputArray[i]) {
                    temp = temp.getChild();
                    i++;
                } else {
                    temp = temp.getSibling();
                }
            }

            while (i < inputArray.length - 1) {
                if (temp.getKey() == inputArray[i] && temp.hasChild()) {
                    i++;
                    temp = temp.getChild();
                } else if (temp.getKey() == inputArray[i] && !temp.hasChild()) {
                    i++;
                    temp.setChild(new TrieNode(inputArray[i], null, null));
                    temp = temp.getChild();
                } else {
                    temp.setSibling(new TrieNode(inputArray[i], null, null));
                    temp = temp.getSibling();
                }
            }
        }
    }

    /**
     * Indicates whether a node is a endOfWord
     */
    private boolean isEndOfWord(TrieNode current) {
        return !current.hasSibling() && !current.hasChild();
    }

    /**
     * @param input string to autocomplete by returning command with matching prefix
     * @return input if the command is not found, matching command word otherwise
     */
    public String attemptAutoComplete(String input) throws NullPointerException {
        StringBuilder output = new StringBuilder();

        char[] inputArray = input.toLowerCase().toCharArray();
        TrieNode temp = root;
        int i = 0;

        while (!isEndOfWord(temp)) {
            if (i < inputArray.length) {
                if (temp.getKey() == inputArray[i]) {
                    output.append(inputArray[i]);
                    temp = temp.getChild();
                    i++;
                } else {
                    temp = temp.getSibling();
                }
            } else if (temp.hasSibling()) {
                //more than one matching command(to enhance in v1.3)
                return input;
            } else {
                output.append(temp.getKey());
                temp = temp.getChild();
            }
        }
        output.append(temp.getKey());

        return output.toString();
    }



}
