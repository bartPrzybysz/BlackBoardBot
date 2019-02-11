package controllers;

import blackboardbot.ConstraintSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    @FXML
    Label calendarLabel;

    @FXML
    TextField courseUrl, calendarUrl;

    @FXML
    ChoiceBox yearChoice, sessionChoice;

    @FXML
    TextArea constraintArea;

    @FXML
    Button submitButton;

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
                calendarLabel.setVisible(true);
                calendarLabel.setManaged(true);
                calendarUrl.setVisible(true);
                calendarUrl.setManaged(true);
                break;
            default:
                singleButton.setSelected(true);
                inputSingle.setVisible(true);
                break;
        }

        if(PackageVars.courseUrl != null) courseUrl.setText(PackageVars.courseUrl);
        if(PackageVars.calendarUrl != null) calendarUrl.setText(PackageVars.calendarUrl);
        if(PackageVars.year != null) yearChoice.getSelectionModel().select(PackageVars.year);
        if(PackageVars.session != null) sessionChoice.getSelectionModel().select(PackageVars.session);
        if(PackageVars.constraints != null) {
            constraintArea.clear();
            constraintArea.appendText(PackageVars.constraints.toString());
        }
    }


    public void handleToggle(ActionEvent actionEvent) {
        setPaneVisibility(paneMap.get(targetTypeToggleGroup.getSelectedToggle()));
        if (targetTypeToggleGroup.getSelectedToggle() == null){
            submitButton.setDisable(true);
        } else {
            submitButton.setDisable(false);
        }
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
        if(targetTypeToggleGroup.getSelectedToggle() == singleButton) {
            PackageVars.targetType = TargetType.SINGLE;
        } else {
            PackageVars.targetType = TargetType.CONSTRAINT;
        }

        PackageVars.courseUrl = courseUrl.getText();
        PackageVars.calendarUrl = calendarUrl.getText();
        if (yearChoice.getSelectionModel().getSelectedItem() != null) {
            PackageVars.year = yearChoice.getSelectionModel().getSelectedItem().toString();
        }
        if (sessionChoice.getSelectionModel().getSelectedItem() != null) {
            PackageVars.session = sessionChoice.getSelectionModel().getSelectedItem().toString();
        }
        if (constraintArea.getText() != null) PackageVars.constraints = new ConstraintSet(constraintArea.getText());
    }
}
