package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AVATAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_JERSEY_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_POSITION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.ParserUtil.UNSPECIFIED_FIELD;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.DeselectTeamEvent;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Avatar;
import seedu.address.model.person.Email;
import seedu.address.model.person.JerseyNumber;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Position;
import seedu.address.model.person.Rating;
import seedu.address.model.person.Remark;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.team.TeamName;

/**
 * Edits the details of an existing player in the address book.
 */
public class EditCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_ALIAS = "e";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the player identified "
            + "by the index number used in the last player listing. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_RATING + "RATING] "
            + "[" + PREFIX_POSITION + "POSITION] "
            + "[" + PREFIX_JERSEY_NUMBER + "JERSEY_NUMBER] "
            + "[" + PREFIX_AVATAR + "AVATAR] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_PARAMETERS = "INDEX "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_RATING + "RATING] "
            + "[" + PREFIX_POSITION + "POSITION] "
            + "[" + PREFIX_JERSEY_NUMBER + "JERSEY_NUMBER] "
            + "[" + PREFIX_AVATAR + "AVATAR] "
            + "[" + PREFIX_TAG + "TAG]";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This player already exists in the address book.";
    public static final String MESSAGE_FILE_NOT_FOUND = "Avatar image file specified does not exist";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    private Person personToEdit;
    private Person editedPerson;

    /**
     * @param index of the player in the filtered player list to edit
     * @param editPersonDescriptor details to edit the player with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            if (!editedPerson.getAvatar().toString().equals(UNSPECIFIED_FIELD)) {
                editedPerson.getAvatar().setFilePath(editedPerson.getName().fullName);
            }
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target player cannot be missing");
        } catch (IOException e) {
            throw new CommandException(MESSAGE_FILE_NOT_FOUND);
        }
        EventsCenter.getInstance().post(new DeselectTeamEvent());
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToEdit = lastShownList.get(index.getZeroBased());
        editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     * NOTE: Private fields will not be edited.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = (personToEdit.getPhone().isPrivate())
                ? personToEdit.getPhone() : editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = (personToEdit.getEmail().isPrivate())
                ? personToEdit.getEmail() : editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = (personToEdit.getAddress().isPrivate())
                ? personToEdit.getAddress() : editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Remark updatedRemark = (personToEdit.getRemark().isPrivate())
                ? personToEdit.getRemark() : personToEdit.getRemark();
        TeamName updatedTeamName = personToEdit.getTeamName();
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Rating updatedRating = editPersonDescriptor.getRating().orElse(personToEdit.getRating());
        JerseyNumber updatedJerseyNumber = editPersonDescriptor.getJerseyNumber()
                .orElse(personToEdit.getJerseyNumber());
        Position updatedPosition = editPersonDescriptor.getPosition().orElse(personToEdit.getPosition());
        Avatar updatedAvatar = editPersonDescriptor.getAvatar().orElse(personToEdit.getAvatar());


        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedRemark,
                updatedTeamName, updatedTags, updatedRating, updatedPosition, updatedJerseyNumber, updatedAvatar);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        EditCommand e = (EditCommand) other;
        return index.equals(e.index)
                && editPersonDescriptor.equals(e.editPersonDescriptor)
                && Objects.equals(personToEdit, e.personToEdit);
    }

    /**
     * Stores the details to edit the player with. Each non-empty field value will replace the
     * corresponding field value of the player.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Rating rating;
        private Position position;
        private JerseyNumber jerseyNumber;
        private Avatar avatar;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setRating(toCopy.rating);
            setPosition(toCopy.position);
            setJerseyNumber(toCopy.jerseyNumber);
            setAvatar(toCopy.avatar);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(this.name, this.phone, this.email, this.address, this.tags,
                    this.rating, this.position, this.jerseyNumber, this.avatar);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public void setRating(Rating rating) {
            this.rating = rating;
        }

        public Optional<Rating> getRating() {
            return Optional.ofNullable(rating);
        }

        public void setPosition(Position position) {
            this.position = position;
        }

        public Optional<Position> getPosition() {
            return Optional.ofNullable(position);
        }

        public void setJerseyNumber(JerseyNumber jerseyNumber) {
            this.jerseyNumber = jerseyNumber;
        }

        public Optional<JerseyNumber> getJerseyNumber() {
            return Optional.ofNullable(jerseyNumber);
        }

        public void setAvatar(Avatar avatar) {
            this.avatar = avatar;
        }

        public Optional<Avatar> getAvatar() {
            return Optional.ofNullable(avatar);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            // state check
            EditPersonDescriptor e = (EditPersonDescriptor) other;

            return getName().equals(e.getName())
                    && getPhone().equals(e.getPhone())
                    && getEmail().equals(e.getEmail())
                    && getAddress().equals(e.getAddress())
                    && getTags().equals(e.getTags())
                    && getRating().equals(e.getRating())
                    && getPosition().equals(e.getPosition())
                    && getJerseyNumber().equals(e.getJerseyNumber())
                    && getAvatar().equals(e.getAvatar());
        }
    }
}
