package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.logic.parser.ParserUtil.UNSPECIFIED_FIELD;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalTeams.ARSENAL;
import static seedu.address.testutil.TypicalTeams.CHELSEA;
import static seedu.address.testutil.TypicalTeams.LIVERPOOL;
import static seedu.address.testutil.TypicalTeams.getTypicalAddressBook;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.HighlightSelectedTeamEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.team.Team;
import seedu.address.model.team.TeamName;
import seedu.address.ui.testutil.EventsCollectorRule;

//@@author jordancjq
public class AssignCommandTest {

    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_validTeamAndIndexUnfilteredList_success() {
        Index lastPersonIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Team firstTeam = model.getAddressBook().getTeamList().get(0);

        assertExecutionSuccess(firstTeam, Collections.singletonList(lastPersonIndex));
    }

    @Test
    public void execute_validTeamAndIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Team firstTeam = model.getAddressBook().getTeamList().get(0);

        assertExecutionSuccess(firstTeam, Collections.singletonList(INDEX_FIRST_PERSON));
    }

    @Test
    public void execute_validTeamAndInvalidIndexUnfilteredList_fail() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        Team firstTeam = model.getAddressBook().getTeamList().get(0);

        assertExecutionFailure(firstTeam, Collections.singletonList(outOfBoundsIndex),
                Messages.MESSAGE_INVALID_ALL_INDEX);
    }

    @Test
    public void execute_invalidTeamAndValidIndexUnfilteredList_fail() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Team invalidTeam = ARSENAL;

        assertExecutionFailure(invalidTeam, Collections.singletonList(outOfBoundsIndex),
                Messages.MESSAGE_TEAM_NOT_FOUND);
    }

    @Test
    public void execute_validTeamAndValidIndexUnfilteredListPersonExistInTeam_fail() throws Exception {
        Team firstTeam = model.getAddressBook().getTeamList().get(0);

        AssignCommand assignCommand = prepareCommand(firstTeam.getTeamName(),
                Collections.singletonList(INDEX_FIRST_PERSON));

        assignCommand.execute();
        eventsCollectorRule.eventsCollector.reset();

        assertExecutionFailure(firstTeam, Collections.singletonList(INDEX_FIRST_PERSON),
               AssignCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void equals() {
        final AssignCommand standardCommand = prepareCommand(CHELSEA.getTeamName(),
                Collections.singletonList(INDEX_FIRST_PERSON));
        final AssignCommand secondCommand = prepareCommand(LIVERPOOL.getTeamName(),
                Collections.singletonList(INDEX_SECOND_PERSON));

        // same values -> returns true
        AssignCommand commandWithSameValues = prepareCommand(CHELSEA.getTeamName(),
                Collections.singletonList(INDEX_FIRST_PERSON));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different type -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different team name and index -> returns false
        assertFalse(standardCommand.equals(secondCommand));
    }

    /**
     * Returns an {@code AssignCommand} with parameters {@code team}.
     */
    private AssignCommand prepareCommand(TeamName team, List<Index> indexes) {
        AssignCommand assignCommand = new AssignCommand(team, indexes);
        assignCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return assignCommand;
    }

    /**
     * Executes a {@code AssignCommand} with the given {@code team} and {@code indexes},
     * and checks that {@code HighlightSelectedTeamEvent} is raised with the correct team name.
     */
    private void assertExecutionSuccess(Team team, List<Index> indexes) {
        AssignCommand assignCommand = prepareCommand(team.getTeamName(), indexes);
        String expectedAssignResultMessage = new String();

        for (Index index : indexes) {
            Person person = model.getFilteredPersonList().get(index.getZeroBased());
            if (person.getTeamName().toString().equals(UNSPECIFIED_FIELD)) {
                expectedAssignResultMessage = String.format(AssignCommand.MESSAGE_UNSPECIFIED_TEAM_SUCCESS,
                        person.getName().toString(), team.getTeamName().toString());
            } else {
                expectedAssignResultMessage = String.format(AssignCommand.MESSAGE_TEAM_TO_TEAM_SUCCESS,
                        person.getName().toString(), person.getTeamName().toString(), team.getTeamName().toString());
            }
        }

        try {
            CommandResult commandResult = assignCommand.execute();
            assertEquals(String.format(AssignCommand.MESSAGE_SUCCESS + expectedAssignResultMessage),
                    commandResult.feedbackToUser);
        } catch (CommandException ce) {
            throw new IllegalArgumentException("Execution of command should not fail.", ce);
        }

        HighlightSelectedTeamEvent lastEvent =
                (HighlightSelectedTeamEvent) eventsCollectorRule.eventsCollector.getMostRecent();
        assertEquals(team.getTeamName().toString(), lastEvent.teamName);

        try {
            for (Person person : model.getFilteredPersonList()) {
                team.remove(person);
            }
        } catch (PersonNotFoundException e) {
            throw new AssertionError("not possible");
        }
    }

    /**
     * Executes a {@code AssignCommand} with the given {@code team} and {@code indexes},
     * and checks that a {@code CommandException} is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Team team, List<Index> indexes, String expectedMessage) {
        AssignCommand assignCommand = prepareCommand(team.getTeamName(), indexes);

        try {
            assignCommand.execute();
            fail("The expected CommandException was not thrown.");
        } catch (CommandException ce) {
            assertEquals(expectedMessage, ce.getMessage());
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        }
    }
}
