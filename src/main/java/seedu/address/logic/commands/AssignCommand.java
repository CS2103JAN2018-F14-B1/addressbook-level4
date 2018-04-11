package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TEAM_NAME;
import static seedu.address.logic.parser.ParserUtil.UNSPECIFIED_FIELD;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.HighlightSelectedTeamEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.team.TeamName;
import seedu.address.model.team.exceptions.TeamNotFoundException;

//@@author jordancjq
/**
 * Assigns a person to a team.
 */
public class AssignCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "assign";
    public static final String COMMAND_ALIAS = "at";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns a player or a group of players to a team "
            + "by the index number used in the last player listing.\n"
            + "You can also unassign a player by leaving out TEAM_NAME as an input.\n"
            + "Team of the player will be updated and will be added to team.\n"
            + "Only 1 team can be assigned to each player.\n"
            + "Parameters: "
            + PREFIX_TEAM_NAME + "TEAM_NAME "
            + PREFIX_INDEX + "INDEX (must be a positive integer) "
            + "[INDEX]...\n"
            + "Example: " + COMMAND_WORD + " "
            + "Arsenal "
            + PREFIX_INDEX + "1 2";

    public static final String MESSAGE_PARAMETERS = "[TEAM_NAME] "
            + PREFIX_INDEX + "INDEX "
            + "[INDEX]...";

    public static final String MESSAGE_SUCCESS = "Players successfully assigned to team.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the team";
    public static final String MESSAGE_TEAM_TO_TEAM_SUCCESS = "\n%1$s has been assigned from %2$s to %3$s.";
    public static final String MESSAGE_UNSPECIFIED_TEAM_SUCCESS = "\n%1$s has been assigned from to %2$s.";

    private final TeamName targetTeam;
    private final List<Index> targetIndexes;

    private List<Person> personsToAssign;

    /**
     * Creates an AssignCommand to assign person to {@code Team}
     */
    public AssignCommand(TeamName targetTeam, List<Index> targetIndexes) {
        requireNonNull(targetTeam);
        requireNonNull(targetIndexes);

        this.targetTeam = targetTeam;
        this.targetIndexes = targetIndexes;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        String successfulPlayerAssignedMessage = new String();
        try {
            for (Person person : personsToAssign) {
                model.assignPersonToTeam(person, targetTeam);
                if (person.getTeamName().toString().equals(UNSPECIFIED_FIELD)) {
                    successfulPlayerAssignedMessage += String.format(MESSAGE_UNSPECIFIED_TEAM_SUCCESS,
                            person.getName().toString(), targetTeam.toString());
                } else {
                    successfulPlayerAssignedMessage += String.format(MESSAGE_TEAM_TO_TEAM_SUCCESS,
                            person.getName().toString(), person.getTeamName().toString(), targetTeam.toString());
                }

            }
            model.updateFilteredPersonList(targetTeam);
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (TeamNotFoundException tnfe) {
            throw new AssertionError("Impossible: Team should exist in this addressbook");
        }

        EventsCenter.getInstance().post(new HighlightSelectedTeamEvent(targetTeam.toString()));
        return new CommandResult(MESSAGE_SUCCESS + successfulPlayerAssignedMessage);
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        if (!model.getAddressBook().getTeamList().stream().anyMatch(t -> t.getTeamName().equals(targetTeam))) {
            throw new CommandException(Messages.MESSAGE_TEAM_NOT_FOUND);
        }

        List<Person> lastShownList = model.getFilteredPersonList();
        List<Index> executableIndexes = new ArrayList<>();
        Boolean canAssignPerson = false;

        for (Index idx : targetIndexes) {
            if (idx.getZeroBased() < lastShownList.size()) {
                executableIndexes.add(idx);
                canAssignPerson = true;
            }
        }

        if (!canAssignPerson) {
            throw new CommandException(Messages.MESSAGE_INVALID_ALL_INDEX);
        }

        personsToAssign = new ArrayList<>();
        executableIndexes.forEach(idx -> personsToAssign.add(lastShownList.get(idx.getZeroBased())));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AssignCommand // instanceof handles nulls
                && this.targetTeam.equals(((AssignCommand) other).targetTeam)) // state check
                && this.targetIndexes.equals(((AssignCommand) other).targetIndexes);
    }
}
