package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TEAMNAME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.team.TeamName;

/**
 * Assigns a person to a team.
 */
public class AssignCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "assign";
    public static final String COMMAND_ALIAS = "at";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Assigns a person or a group of person to a team "
            + "by the index number used in the last person listing.\n"
            + "Team of the person will be updated and will be added to team.\n"
            + "Parameters: "
            + PREFIX_TEAMNAME + "TEAM_NAME "
            + PREFIX_INDEX + "INDEX (must be a positive integer) "
            + "[INDEX]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_TEAMNAME + "Arsenal "
            + PREFIX_INDEX + "1 2";

    public static final String MESSAGE_PARAMETERS = PREFIX_TEAMNAME + "TEAM_NAME "
            + PREFIX_INDEX + "INDEX "
            + "[INDEX]...";

    public static final String MESSAGE_SUCCESS = "Person assigned to team";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the team";

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
        try {
            for (Person person : personsToAssign) {
                model.assignPersonToTeam(person, targetTeam);
            }
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
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