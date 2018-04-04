package seedu.address.ui;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.ui.ChangeTagColourEvent;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;
    private final Logger logger = LogsCenter.getLogger(PersonCard.class);

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label id;
    @FXML
    private Label jerseyNumber;
    @FXML
    private Label teamName;
    @FXML
    private Label remark;
    @FXML
    private FlowPane tags;
    @FXML
    private Label rating;
    @FXML
    private Label position;

    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);

        if (person.getPhone().isPrivate()) {
            phone.setText(person.getPhone().toString());
        } else {
            phone.setText(person.getPhone().value);
        }

        if (person.getAddress().isPrivate()) {
            address.setText(person.getAddress().toString());
        } else {
            address.setText(person.getAddress().value);
        }

        if (person.getEmail().isPrivate()) {
            email.setText(person.getEmail().toString());
        } else {
            email.setText(person.getEmail().value);
        }

        if (person.getRemark().isPrivate()) {
            remark.setText(person.getRemark().toString());
        } else {
            remark.setText(person.getRemark().value);
        }

        if (person.getRating().isPrivate()) {
            rating.setText(person.getRating().toString());
        } else {
            rating.setText(person.getRating().value);
        }

        teamName.setText(person.getTeamName().fullName);
        position.setText(person.getPosition().getPositionName());
        jerseyNumber.setText(person.getJerseyNumber().toString());

        initTags(person);
        registerAsAnEventHandler(this);
    }

    /**
     * Creates the tag labels for {@code person}.
     */
    private void initTags(Person person) {
        person.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.getTagName());
            tagLabel.getStyleClass().add(tag.getTagColour());
            tags.getChildren().add(tagLabel);
        });
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonCard)) {
            return false;
        }

        // state check
        PersonCard card = (PersonCard) other;
        return id.getText().equals(card.id.getText())
                && person.equals(card.person);
    }

    @Subscribe
    public void handleColourChangeEvent(ChangeTagColourEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        Set<Tag> tagSet = person.getTags();
        int i = 0;
        for (Iterator<Tag> it = tagSet.iterator(); it.hasNext();) {
            Tag tag = it.next();
            if (tag.getTagName().equals(event.tagName)) {
                tags.getChildren().remove(i);
                Label newTagLabel = new Label(event.tagName);
                newTagLabel.getStyleClass().add(event.tagColour);
                tags.getChildren().add(i, newTagLabel);
            }
            i++;
        }
    }
}
