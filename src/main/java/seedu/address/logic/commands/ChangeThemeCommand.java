package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.events.ui.ChangeThemeEvent;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.ui.MainWindow;

/**
 * Changes the theme of the Address Book.
 */
/** @@author Codee */
public class ChangeThemeCommand extends Command {

    public static final String COMMAND_WORD = "changeTheme";
    public static final String COMMAND_ALIAS = "cte";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Changes the theme of MTM.\n"
            + "Parameters: THEME (must be either Light, or Dark)\n"
            + "Examples: changeTheme Light, cte Dark";

    public static final String MESSAGE_PARAMETERS = "THEME (Light or Dark)";

    public static final String MESSAGE_THEME_SUCCESS = "Theme updated to: %1$s";

    private final String theme;

    public ChangeThemeCommand(String theme) {
        this.theme = theme.trim();
    }

    @Override
    public CommandResult execute() throws CommandException {
        if (!isValidTheme(this.theme)) {
            throw new CommandException(Messages.MESSAGE_INVALID_THEME);
        }
        if ((MainWindow.getCurrentTheme()).contains(this.theme)) {
            throw new CommandException("Theme is already set to " + this.theme + "!");
        }
        EventsCenter.getInstance().post(new ChangeThemeEvent(this.theme));
        return new CommandResult(String.format(MESSAGE_THEME_SUCCESS, this.theme));
    }

    private boolean isValidTheme(String theme) {
        return theme.equals("Light") || theme.equals("Dark");
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ChangeThemeCommand // instanceof handles nulls
                && this.theme.equals(((ChangeThemeCommand) other).theme)); // state check
    }

}
