package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.tag.Tag;

/**
 * This is the unit test for {@code SetCommand}.
 */
public class SetCommandTest {

    private Model model;
    private Tag tagOne = new Tag("testTagOne");
    private Tag tagTwo = new Tag("testTagTwo");

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void equals() {

        SetCommand testCommand = new SetCommand(tagOne, "teal");
        SetCommand testCommandTwo = new SetCommand(tagOne, "teal");
        //Test to ensure command is strictly a SetCommand
        assertFalse(testCommand.equals(new AddCommand(CARL)));
        assertFalse(testCommand.equals(new ClearCommand()));
        assertFalse(testCommand.equals(new DeleteCommand(INDEX_FIRST_PERSON)));
        assertFalse(testCommand.equals(new HistoryCommand()));
        assertFalse(testCommand.equals(new HelpCommand()));
        assertFalse(testCommand.equals(new RedoCommand()));
        assertFalse(testCommand.equals(new UndoCommand()));
        assertFalse(testCommand.equals(new ListCommand()));
        assertFalse(testCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_AMY)));


        //Test to check that different tag string returns false
        assertFalse(testCommand.equals(new SetCommand(tagTwo, "teal")));
        assertFalse(testCommandTwo.equals(new SetCommand(tagTwo, "teal")));

        //Test to check that different color strings returns false
        assertFalse(testCommand.equals(new SetCommand(tagOne, "red")));
        assertFalse(testCommandTwo.equals(new SetCommand(tagTwo, "red")));
    }

    @Test
    public void checkCommandResult() throws CommandException {

        //Check if the result message is correct when there is no tags found
        SetCommand command = new SetCommand(new Tag("blablabla"), "teal");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        assertTrue(command.execute().feedbackToUser.equals("tag is invalid! Please input a valid tag name!"));

        //When tags is present
        command = new SetCommand(new Tag("friends"), "red");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        assertFalse(command.execute().feedbackToUser.equals("No such tag"));
        assertTrue(command.execute().feedbackToUser.equals("tag [friends] colour changed to red"));

        //Check if friends tags are set to color
        command = new SetCommand(new Tag("friends"), "teal");
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        assertTrue(command.execute().feedbackToUser.equals("tag [friends] colour changed to teal"));
        for (Tag tag : model.getAddressBook().getTagList()) {
            if ("friends".equals(tag.tagName)) {
                assertTrue(tag.getTagColour().equals("teal"));
                assertFalse(tag.getTagColour().equals("pink"));
            }
        }
        resetAllTagsToDefault();
    }

    /**
     * This method allows all tags to be set to the default colour "teal"
     */
    public void resetAllTagsToDefault() {
        ObservableList<Tag> allTags = model.getAddressBook().getTagList();
        for (Tag t : allTags) {
            t.changeTagColour("teal");
        }
    }
}
