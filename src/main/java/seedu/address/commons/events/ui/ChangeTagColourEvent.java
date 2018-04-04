package seedu.address.commons.events.ui;

import seedu.address.commons.events.BaseEvent;

/**
 * Indicates a request to change tag colour
 */
public class ChangeTagColourEvent extends BaseEvent {

    public final String tagColour;
    public final String tagName;

    public ChangeTagColourEvent(String tagName, String tagColour) {
        this.tagName = tagName;
        this.tagColour = tagColour;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
