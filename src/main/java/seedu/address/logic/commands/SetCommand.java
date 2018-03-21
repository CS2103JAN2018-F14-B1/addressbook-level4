package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COLOUR;

import seedu.address.model.tag.Tag;

/**
 * Adds a colour to a tag in address book.
 */
public class SetCommand extends Command {

    public static final String COMMAND_WORD = "setTagColour";
    public static final String COMMAND_ALIAS = "stc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets the colour of tags to the colour of choice "
            + "Parameters: "
            + "[" + PREFIX_TAG + "TAG] "
            + "[" + PREFIX_TAG_COLOUR + "TAG_COLOUR]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG_COLOUR + "green\n"
            + "Colours to choose from are : teal, red, yellow, blue, orange, brown, \n"
            + "green, pink, black, grey\n";

    public static final String MESSAGE_INVALID_TAG = "tag is invalid! Please input a valid tag name!";
    public static final String MESSAGE_SUCCESS = "tag %1$s colour changed to %2$s";

    private final Tag tagToSet;
    private final String tagColour;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public SetCommand(Tag tag, String colour) {
        requireNonNull(tag);
        tagToSet = tag;
        tagColour = colour;
    }

    @Override
    public CommandResult execute() {
        requireNonNull(model);
        boolean isTagValid = model.setTagColour(tagToSet, tagColour);
        if (!tagToSet.isValidTagName(tagToSet.getTagName()) || !isTagValid) {
            return new CommandResult(String.format(MESSAGE_INVALID_TAG));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, tagToSet.toString(), tagColour));
    }

    @Override
    public boolean equals(Object other) {
        // Check if
        // (a) Object is the same object
        // (b) Object is an instance of the object and that toSet, tag and color are the same
        return other == this // short circuit if same object
                || (other instanceof SetCommand // instanceof handles nulls
                && this.tagToSet.getTagName().equals(((SetCommand) other).tagToSet.getTagName()))
                && this.tagColour.equals(((SetCommand) other).tagColour);
    }

}

