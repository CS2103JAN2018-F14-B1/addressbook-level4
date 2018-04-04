package seedu.address.ui;

import java.util.List;

import com.google.common.eventbus.Subscribe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import seedu.address.commons.events.ui.DeselectTeamEvent;
import seedu.address.commons.events.ui.HighlightSelectedTeamEvent;
import seedu.address.commons.events.ui.ShowNewTeamNameEvent;
import seedu.address.model.team.Team;

/**
 * A ui for displaying the team currently chosen
 */
public class TeamDisplay extends UiPart<Region> {

    private static final String FXML = "TeamDisplay.fxml";
    private ObservableList<Team> teamList;

    @FXML
    private FlowPane teams;

    public TeamDisplay(ObservableList<Team> teamList) {
        super(FXML);
        this.teamList = teamList;
        initTeams();
        getTeams();
        registerAsAnEventHandler(this);
    }

    /**
     * Creates the tag labels for {@code person}.
     */
    private void initTeams() {
        for (Team t: this.teamList) {
            Label teamLabel = new Label(t.getTeamName().toString());
            teamLabel.setStyle("-fx-text-fill: #9cb3d8");
            teams.getChildren().add(teamLabel);
            teams.setHgap(10);
        }
    }

    public List<String> getTeams() {
        List<String> listOfTeams = FXCollections.observableArrayList();
        for (Team t: teamList) {
            listOfTeams.add(t.getTeamName().toString());
        }
        return listOfTeams;
    }

    @Subscribe
    private void handleShowNewTeamEvent(ShowNewTeamNameEvent event) {
        Label newTeamLabel = new Label(event.teamName);
        newTeamLabel.getStyleClass();
        teams.getChildren().add(newTeamLabel);
    }

    @Subscribe
    private void handleHighlightSelectedTeamEvent(HighlightSelectedTeamEvent event) {
        for (int i = 0; i < teamList.size(); i++) {
            if (event.teamName.equals(teamList.get(i).getTeamName().toString())) {
                teams.getChildren().remove(i);
                Label newTeamLabel = new Label(event.teamName);
                newTeamLabel.getStyleClass().add("selected");
                teams.getChildren().add(i, newTeamLabel);
            } else {
                teams.getChildren().remove(i);
                Label newTeamLabel = new Label(teamList.get(i).getTeamName().toString());
                newTeamLabel.getStyleClass();
                teams.getChildren().add(i, newTeamLabel);
            }
        }
    }

    @Subscribe
    private void handleDeselectTeamEvent(DeselectTeamEvent event) {
        for (int i = 0; i < teamList.size(); i++) {
            teams.getChildren().remove(i);
            Label newTeamLabel = new Label(teamList.get(i).getTeamName().toString());
            newTeamLabel.getStyleClass();
            teams.getChildren().add(i, newTeamLabel);
        }
    }
}

