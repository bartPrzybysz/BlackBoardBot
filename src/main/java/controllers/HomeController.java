package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class HomeController {
    @FXML
    ToggleGroup actionToggleGroup;

    @FXML
    ToggleButton revstatButton, titleColorButton, removeIconsButton, toggleAvailabilityButton, setLandingButton,
            checkDatesButton;

    @FXML
    Pane introPage, revstatPage, titleColorPage, removeIconsPage, toggleAvailabilityPage, setLandingPage, checkDatesPage;

    @FXML
    Button submitButton;

    private HashMap<ToggleButton, Pane> paneMap;
    private HashMap<ToggleButton, Action> actionMap;

    @FXML
    public void initialize() {
        paneMap = new HashMap<>();

        paneMap.put(revstatButton, revstatPage);
        paneMap.put(titleColorButton, titleColorPage);
        paneMap.put(removeIconsButton, removeIconsPage);
        paneMap.put(toggleAvailabilityButton, toggleAvailabilityPage);
        paneMap.put(setLandingButton, setLandingPage);
        paneMap.put(checkDatesButton, checkDatesPage);

        actionMap = new HashMap<>();

        actionMap.put(revstatButton, Action.REVSTAT);
        actionMap.put(titleColorButton, Action.TITLE_COLOR);
        actionMap.put(removeIconsButton, Action.REMOVE_ICONS);
        actionMap.put(toggleAvailabilityButton, Action.TOGGLE_AVAILABILITY);
        actionMap.put(setLandingButton, Action.SET_LANDING);
        actionMap.put(checkDatesButton, Action.CHECK_DATES);
    }

    @FXML
    public void handleToggle(ActionEvent actionEvent) {
        if (actionToggleGroup.getSelectedToggle() == null) {
            submitButton.setDisable(true);
            setPaneVisibility(introPage);
        } else {
            submitButton.setDisable(false);
            setPaneVisibility(paneMap.get(actionToggleGroup.getSelectedToggle()));
        }
    }

    private void setPaneVisibility(Pane activePane) {
        ArrayList<Pane> panes = new ArrayList<Pane>();

        panes.add(introPage);
        panes.add(revstatPage);
        panes.add(titleColorPage);
        panes.add(removeIconsPage);
        panes.add(toggleAvailabilityPage);
        panes.add(setLandingPage);
        panes.add(checkDatesPage);

        panes.remove(activePane);

        for(Pane p : panes) {
            p.setVisible(false);
        }

        activePane.setVisible(true);
    }

    @FXML
    public void handleSubmit(ActionEvent actionEvent) {
        PackageVars.action = actionMap.get(actionToggleGroup.getSelectedToggle());

        Scene scene = ((Node) actionEvent.getSource()).getScene();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/target.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene.setRoot(root);
    }
}
