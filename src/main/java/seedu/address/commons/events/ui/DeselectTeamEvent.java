package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * Indicates a request to deselected selected teams.
 */
public class DeselectTeamEvent extends BaseEvent {

    public DeselectTeamEvent() {
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
