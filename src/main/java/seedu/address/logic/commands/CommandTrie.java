package seedu.address.logic.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@@author lithiumlkid
/**
 * Trie of possible commands. Stores all possible commands for the addressbook.
 * Used for autocomplete functionality by returning a possible command via the attemptAutoComplete function.
 */
public class CommandTrie {

    private TrieNode root = null;
    private Set<String> commandSet;
    private Map<String, String> commandMap;

    public CommandTrie() {
        commandSet = Stream.of(
                EditCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
                AddCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
                HelpCommand.COMMAND_WORD, SelectCommand.COMMAND_WORD, HistoryCommand.COMMAND_WORD,
                ListCommand.COMMAND_WORD, RedoCommand.COMMAND_WORD,  UndoCommand.COMMAND_WORD,
                SortCommand.COMMAND_WORD, SetCommand.COMMAND_WORD, RemarkCommand.COMMAND_WORD,
                CreateCommand.COMMAND_WORD, RemoveCommand.COMMAND_WORD, AssignCommand.COMMAND_WORD,
                RenameCommand.COMMAND_WORD, ViewCommand.COMMAND_WORD, ChangeThemeCommand.COMMAND_WORD,
                KeyCommand.COMMAND_WORD, TogglePrivacyCommand.COMMAND_WORD
        ).collect(Collectors.toSet());

        for (String command : commandSet) {
            this.insert(command);
        }

        commandMap = new HashMap<>();
        commandMap.put(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_PARAMETERS);
        commandMap.put(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_PARAMETERS);
        commandMap.put(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_PARAMETERS);
        commandMap.put(FindCommand.COMMAND_WORD, FindCommand.MESSAGE_PARAMETERS);
        commandMap.put(SelectCommand.COMMAND_WORD, SelectCommand.MESSAGE_PARAMETERS);
        commandMap.put(SortCommand.COMMAND_WORD, SortCommand.MESSAGE_PARAMETERS);
        commandMap.put(SetCommand.COMMAND_WORD, SetCommand.MESSAGE_PARAMETERS);
        commandMap.put(RemarkCommand.COMMAND_WORD, RemarkCommand.MESSAGE_PARAMETERS);
        commandMap.put(CreateCommand.COMMAND_WORD, CreateCommand.MESSAGE_PARAMETERS);
        commandMap.put(RemoveCommand.COMMAND_WORD, RemoveCommand.MESSAGE_PARAMETERS);
        commandMap.put(AssignCommand.COMMAND_WORD, AssignCommand.MESSAGE_PARAMETERS);
        commandMap.put(RenameCommand.COMMAND_WORD, RenameCommand.MESSAGE_PARAMETERS);
        commandMap.put(ViewCommand.COMMAND_WORD, ViewCommand.MESSAGE_PARAMETERS);
        commandMap.put(ChangeThemeCommand.COMMAND_WORD, ChangeThemeCommand.MESSAGE_PARAMETERS);
        commandMap.put(KeyCommand.COMMAND_WORD, KeyCommand.MESSAGE_PARAMETERS);
        commandMap.put(TogglePrivacyCommand.COMMAND_WORD, TogglePrivacyCommand.MESSAGE_PARAMETERS);
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

        if (commandSet.contains(input)) {
            if (commandMap.containsKey(input)) {
                output.append(" ");
                output.append(commandMap.get(input));
            }
        } else {
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
                    return input;
                } else {
                    output.append(temp.getKey());
                    temp = temp.getChild();
                }
            }
            output.append(temp.getKey());
        }
        return output.toString();
    }

    public Set<String> getCommandSet() {
        return commandSet;
    }

    /**
     * @return a list of all possible commands for the given input
     */
    public List<String> getOptions(String input) {
        char[] inputArray = input.toLowerCase().toCharArray();
        TrieNode start = root;
        int i = 0;

        while (!isEndOfWord(start) && i < inputArray.length) {
            if (start.getKey() == inputArray[i]) {
                i++;
                start = start.getChild();
            } else {
                start = start.getSibling();
            }
        }

        return findOptions(start, input);
    }

    /**
     * Traverses the trie and gets possible options
     */
    private List<String> findOptions(TrieNode start, String stub) {
        List<String> options = new ArrayList<>();
        StringBuilder output = new StringBuilder();
        output.append(stub);

        if (isEndOfWord(start)) {
            output.append(start.getKey());
            options.add(output.toString());
            return options;
        }
        if (start.hasSibling()) {
            options.addAll(findOptions(start.getSibling(), output.toString()));
        }
        if (start.hasChild()) {
            output.append(start.getKey());
            options.addAll(findOptions(start.getChild(), output.toString()));
        }
        return options;
    }
}
