package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * Indicates a request to show new team name.
 */
public class ShowNewTeamNameEvent extends BaseEvent {

    public final String teamName;

    public ShowNewTeamNameEvent(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
