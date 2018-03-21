package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final String MESSAGE_TAG_CONSTRAINTS = "Tags names should be a string";
    public static final String MESSAGE_TAG_COLOUR_CONSTRAINTS = "Tag colours should be one of these colours:"
        + "teal, red, yellow, blue, orange, brown, green, pink, black, grey";
    public static final String TAG_VALIDATION_REGEX = "\\p{Alnum}+";
    private static final String[] TAG_COLOR_STYLES =
        { "teal", "red", "yellow", "blue", "orange", "brown", "green", "pink", "black", "grey" };

    public final String tagName;
    private String tagColour;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_TAG_CONSTRAINTS);
        this.tagName = tagName;
        this.tagColour = "teal";
    }

    /**
     * Overloaded constructor for a {@code Tag}.
     *
     * @param tagName A valid tag name
     * @param tagColour A valid tag colour.
     */
    public Tag(String tagName, String tagColour) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_TAG_CONSTRAINTS);
        this.tagName = tagName;
        this.tagColour = tagColour;
    }

    public String getTagName() {
        return this.tagName;
    }

    public String getTagColour() {
        return this.tagColour;
    }

    /**
     * Changes the {@code tagColour} for {@code tagName}'s label
     */
    public void changeTagColour(String colour) {
        this.tagColour = colour;
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.matches(TAG_VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid tag colour.
     */
    public static boolean isValidTagColour(String testColour) {
        for (String tcs : TAG_COLOR_STYLES) {
            if (testColour.equals(tcs)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Tag // instanceof handles nulls
                && this.tagName.equals(((Tag) other).tagName)); // state check
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
