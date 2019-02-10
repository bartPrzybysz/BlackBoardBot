package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeController {
    @FXML
    ToggleGroup actionSelectToggleGroup;

    @FXML
    ToggleButton revstatButton, titleDateButton, removeIconsButton, toggleAvailabilityButton, setLandingButton,
            checkDatesButton;

    @FXML
    Pane introPage, revstatPage, titleColorPage, removeIconsPage, toggleAvailabilityPage, setLandingPage, checkDatesPage;

    @FXML
    Button submitButton;

    public void handleToggle(ActionEvent actionEvent) {
        HashMap<ToggleButton, Pane> paneMap = new HashMap<>();

        paneMap.put(revstatButton, revstatPage);
        paneMap.put(titleDateButton, titleColorPage);
        paneMap.put(removeIconsButton, removeIconsPage);
        paneMap.put(toggleAvailabilityButton, toggleAvailabilityPage);
        paneMap.put(setLandingButton, setLandingPage);
        paneMap.put(checkDatesButton, checkDatesPage);

        if (actionSelectToggleGroup.getSelectedToggle() == null) {
            submitButton.setDisable(true);
            setPaneVisibility(introPage);
        } else {
            submitButton.setDisable(false);
            setPaneVisibility(paneMap.get(actionSelectToggleGroup.getSelectedToggle()));
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

    public void handleSubmit(ActionEvent actionEvent) {
        System.out.println("TODO");
    }
}
