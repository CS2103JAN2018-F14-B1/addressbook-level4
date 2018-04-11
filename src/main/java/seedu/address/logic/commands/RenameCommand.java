package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TEAM_NAME;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.events.ui.HighlightSelectedTeamEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.team.Team;
import seedu.address.model.team.TeamName;
import seedu.address.model.team.exceptions.TeamNotFoundException;

//@@author jordancjq
/**
 * Renames the existing team in the address book.
 */
public class RenameCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "rename";
    public static final String COMMAND_ALIAS = "rnt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Renames the specified team with the new "
            + "given team name.\n"
            + "Parameters: TEAM_NAME "
            + PREFIX_TEAM_NAME + "RENAME_TEAM_NAME\n"
            + "Example: " + COMMAND_WORD
            + " Arsenal "
            + PREFIX_TEAM_NAME + "Neo Arsenal";

    public static final String MESSAGE_PARAMETERS = "TEAM_NAME "
            + PREFIX_TEAM_NAME + "RENAME_TEAM_NAME";

    public static final String MESSAGE_RENAME_SUCCESS = "\"%1$s\" is renamed to \"%2$s\".";
    public static final String MESSAGE_NO_CHANGE = "This team name is already in use, try another name.";

    private final TeamName targetTeam;
    private final TeamName updatedTeamName;

    private Team teamToRename;

    /**
     * @param
     */
    public RenameCommand(TeamName targetTeam, TeamName updatedTeamName) {
        requireAllNonNull(targetTeam, updatedTeamName);

        this.targetTeam = targetTeam;
        this.updatedTeamName = updatedTeamName;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            model.renameTeam(teamToRename, updatedTeamName);
            EventsCenter.getInstance().post(new HighlightSelectedTeamEvent(updatedTeamName.toString()));
            model.updateFilteredPersonList(updatedTeamName);
        } catch (TeamNotFoundException tnfe) {
            throw new CommandException(Messages.MESSAGE_TEAM_NOT_FOUND);
        }

        return new CommandResult(String.format(MESSAGE_RENAME_SUCCESS, targetTeam.toString(),
                updatedTeamName.toString()));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        if (!model.getAddressBook().getTeamList().stream().anyMatch(t -> t.getTeamName().equals(targetTeam))) {
            throw new CommandException(Messages.MESSAGE_TEAM_NOT_FOUND);
        }

        List<Team> teams = model.getAddressBook().getTeamList();
        teamToRename = teams.stream().filter(t -> t.getTeamName().equals(targetTeam)).findFirst().get();

        if (teamToRename.getTeamName().equals(updatedTeamName)) {
            throw new CommandException(MESSAGE_NO_CHANGE);
        }

        if (model.getAddressBook().getTeamList().stream().anyMatch(t -> t.getTeamName().equals(updatedTeamName))) {
            throw new CommandException(MESSAGE_NO_CHANGE);
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RenameCommand)) {
            return false;
        }

        // state check
        RenameCommand e = (RenameCommand) other;
        return Objects.equals(targetTeam, e.targetTeam)
                && updatedTeamName.equals(updatedTeamName);
    }
}
