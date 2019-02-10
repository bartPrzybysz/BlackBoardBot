package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TargetController {
    @FXML
    ToggleGroup targetTypeToggleGroup;

    @FXML
    ToggleButton singleButton, termButton, constraintButton;

    @FXML
    Pane inputSingle, inputTerm, inputConstraint;

    private HashMap<ToggleButton, Pane> paneMap;

    @FXML
    public void initialize() {
        paneMap = new HashMap<>();

        paneMap.put(singleButton, inputSingle);
        paneMap.put(termButton, inputTerm);
        paneMap.put(constraintButton, inputConstraint);

        switch(PackageVars.action) {
            case TOGGLE_AVAILABILITY: case SET_LANDING:
                singleButton.setDisable(true);
                termButton.setSelected(true);
                inputTerm.setVisible(true);
                break;
            case CHECK_DATES:
                termButton.setDisable(true);
                constraintButton.setDisable(true);
                singleButton.setSelected(true);
                inputSingle.setVisible(true);
                break;
            default:
                singleButton.setSelected(true);
                inputSingle.setVisible(true);
                break;
        }
    }

    public void handleToggle(ActionEvent actionEvent) {
        setPaneVisibility(paneMap.get(targetTypeToggleGroup.getSelectedToggle()));
    }

    private void setPaneVisibility(Pane activePane) {
        ArrayList<Pane> panes = new ArrayList<>();

        panes.add(inputSingle);
        panes.add(inputTerm);
        panes.add(inputConstraint);

        panes.remove(activePane);

        for(Pane p : panes) {
            p.setVisible(false);
        }

        if (activePane != null) {
            activePane.setVisible(true);
        }
    }

    public void handleBack(ActionEvent actionEvent) {
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene.setRoot(root);
    }

    public void handleSubmit(ActionEvent actionEvent) {
        System.out.println("TODO");
    }
}
