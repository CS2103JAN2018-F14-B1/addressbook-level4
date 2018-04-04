package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.ShowNewTeamNameEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.team.Team;
import seedu.address.model.team.exceptions.DuplicateTeamException;

/**
 * Creates a team to the application
 */
public class CreateCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "create";
    public static final String COMMAND_ALIAS = "ct";

    public static final String MESSAGE_USAGE = COMMAND_WORD + "Creates a team in MTM. "
            + "Parameters: "
            + "TEAM_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + "Arsenal";

    public static final String MESSAGE_PARAMETERS = "TEAM_NAME";

    public static final String MESSAGE_SUCCESS = "New team created: %1$s";
    public static final String MESSAGE_DUPLICATE_TEAM = "This team already exist in the manager";

    private final Team toCreate;

    /**
     * Creates a CreateCommand to add the specified (@code Team)
     *
     */
    public CreateCommand(Team team) {
        requireNonNull(team);
        this.toCreate = team;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        requireNonNull(model);
        try {
            model.createTeam(toCreate);
        } catch (DuplicateTeamException e) {
            throw new CommandException(MESSAGE_DUPLICATE_TEAM);
        }
        EventsCenter.getInstance().post(new ShowNewTeamNameEvent(toCreate.getTeamName().toString()));
        return new CommandResult(String.format(MESSAGE_SUCCESS, toCreate));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof CreateCommand // instanceof handles nulls
                && toCreate.equals(((CreateCommand) other).toCreate));
    }
}
