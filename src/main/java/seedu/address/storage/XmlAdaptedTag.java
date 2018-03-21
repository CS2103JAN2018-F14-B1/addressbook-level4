package seedu.address.storage;

import javax.xml.bind.annotation.XmlElement;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.tag.Tag;

/**
 * JAXB-friendly adapted version of the Tag.
 */
public class XmlAdaptedTag {

    @XmlElement
    private String tagName;

    @XmlElement
    private String tagColour;

    /**
     * Constructs an XmlAdaptedTag.
     * This is the no-arg constructor that is required by JAXB.
     */
    public XmlAdaptedTag() {}

    /**
     * Constructs a {@code XmlAdaptedTag} with the given {@code tagName}.
     */
    public XmlAdaptedTag(String tagName) {
        this.tagName = tagName;
        this.tagColour = "teal";
    }

    /**
     * Constructs a {@code XmlAdaptedTag} with the given {@code tagName} and {@code tagColour}.
     */
    public XmlAdaptedTag(String tagName, String tagColour) {
        this.tagName = tagName;
        this.tagColour = tagColour;
    }

    /**
     * Converts a given Tag into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created
     */
    public XmlAdaptedTag(Tag source) {
        tagName = source.tagName;
        tagColour = source.getTagColour();
    }

    /**
     * Converts this jaxb-friendly adapted tag object into the model's Tag object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person
     */
    public Tag toModelType() throws IllegalValueException {
        if (!Tag.isValidTagName(tagName)) {
            throw new IllegalValueException(Tag.MESSAGE_TAG_CONSTRAINTS);
        }
        if (!Tag.isValidTagColour(tagColour)) {
            throw new IllegalValueException(Tag.MESSAGE_TAG_COLOUR_CONSTRAINTS);
        }
        return new Tag(tagName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof XmlAdaptedTag)) {
            return false;
        }

        return tagName.equals(((XmlAdaptedTag) other).tagName);
    }
}
