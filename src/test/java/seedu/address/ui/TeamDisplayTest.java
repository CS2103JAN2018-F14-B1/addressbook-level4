package seedu.address.ui;

import static seedu.address.testutil.EventsUtil.postNow;
import static seedu.address.ui.testutil.GuiTestAssert.assertTeamDisplayEquals;

import org.junit.Before;
import org.junit.Test;

import guitests.guihandles.TeamDisplayHandle;
import javafx.collections.ObservableList;
import seedu.address.commons.events.ui.ShowNewTeamNameEvent;
import seedu.address.model.team.Team;
import seedu.address.model.team.TeamName;
import seedu.address.testutil.TeamBuilder;


public class TeamDisplayTest extends GuiUnitTest {

    private static final String NEW_TEAM_NAME = "myTeam";
    private static final ShowNewTeamNameEvent SHOW_NEW_TEAM_NAME_EVENT = new ShowNewTeamNameEvent(NEW_TEAM_NAME);

    private TeamDisplay teamDisplay;
    private TeamDisplayHandle teamDisplayHandle;
    private ObservableList<Team> teamList;

    @Before
    public void setUp() {
        teamList = new TeamBuilder().build();
        teamDisplay = new TeamDisplay(teamList);
        uiPartRule.setUiPart(teamDisplay);
        teamDisplayHandle = new TeamDisplayHandle(teamDisplay.getRoot());
    }

    @Test
    public void display() {
        assertTeamDisplay(teamDisplay, teamList);
    }
    /**
     * Asserts that {@code personCard} displays the details of {@code expectedPerson} correctly and matches
     * {@code expectedId}.
     */
    private void assertTeamDisplay(TeamDisplay teamDisplay, ObservableList<Team> teamlist) {
        guiRobot.pauseForHuman();

        // verify team names are displayed correctly
        assertTeamDisplayEquals(teamDisplay, teamDisplayHandle);
    }

    @Test
    public void handleShowNewTeamNameEvent() {
        postNow(SHOW_NEW_TEAM_NAME_EVENT);

        // verify team names are displayed correctly after event
        guiRobot.pauseForHuman();

        teamList.add(new Team(new TeamName(NEW_TEAM_NAME)));
        TeamDisplay expectedTeamDisplay = new TeamDisplay(teamList);
        teamDisplayHandle = new TeamDisplayHandle(teamDisplay.getRoot());
        // verify team names are displayed correctly
        assertTeamDisplayEquals(expectedTeamDisplay, teamDisplayHandle);
    }
}
