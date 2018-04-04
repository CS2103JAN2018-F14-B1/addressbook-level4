package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * Indicates a request to highlight selected team name.
 */
public class HighlightSelectedTeamEvent extends BaseEvent {

    public final String teamName;

    public HighlightSelectedTeamEvent(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
