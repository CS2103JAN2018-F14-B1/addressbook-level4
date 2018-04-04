package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.events.ui.HighlightSelectedTeamEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.team.TeamName;
import seedu.address.model.team.exceptions.TeamNotFoundException;

/**
 * View a team identified using it's team name from the address book.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";
    public static final String COMMAND_ALIAS = "v";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Views the team identified by the team name.\n"
            + "Parameters: TEAM_NAME\n"
            + "Example: " + COMMAND_WORD + " Arsenal";

    public static final String MESSAGE_PARAMETERS = "TEAM_NAME";
    public static final String MESSAGE_VIEW_TEAM_SUCCESS = "Viewing Team: %1$s";

    private final TeamName targetTeam;

    public ViewCommand(TeamName targetTeam) {
        this.targetTeam = targetTeam;
    }

    @Override
    public CommandResult execute() throws CommandException {
        try {
            model.updateFilteredPersonList(targetTeam);
        } catch (TeamNotFoundException tnfe) {
            throw new CommandException(Messages.MESSAGE_TEAM_NOT_FOUND);
        }
        // TODO: Jump to list of teams event
        EventsCenter.getInstance().post(new HighlightSelectedTeamEvent(targetTeam.toString()));
        return new CommandResult(String.format(MESSAGE_VIEW_TEAM_SUCCESS, targetTeam.toString()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ViewCommand // instanceof handles nulls
                && this.targetTeam.equals(((ViewCommand) other).targetTeam)); // state check
    }
}
