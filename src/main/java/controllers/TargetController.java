package controllers;

import blackboardbot.ConstraintSet;
import javafx.beans.binding.BooleanBinding;
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

    // Relate toggle button to corresponding pane
    private HashMap<ToggleButton, Pane> paneMap;

    @FXML
    public void initialize() {
        paneMap = new HashMap<>();

        paneMap.put(singleButton, inputSingle);
        paneMap.put(termButton, inputTerm);
        paneMap.put(constraintButton, inputConstraint);

        // Disable togglebuttons for illegal targets
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

        // If paramaters already set, display them
        if(PackageVars.courseUrl != null) courseUrl.setText(PackageVars.courseUrl);
        if(PackageVars.calendarUrl != null) calendarUrl.setText(PackageVars.calendarUrl);
        if(PackageVars.year != null) {
            yearChoice.getSelectionModel().select(PackageVars.year);
        } else {
            yearChoice.getSelectionModel().selectFirst();
        }
        if(PackageVars.session != null) {
            sessionChoice.getSelectionModel().select(PackageVars.session);
        } else {
            sessionChoice.getSelectionModel().selectFirst();
        }
        if(PackageVars.constraints != null) {
            constraintArea.clear();
            constraintArea.appendText(PackageVars.constraints.toString());
        }

        //Disable submit button until form filled out
        BooleanBinding formFilledOut = new BooleanBinding() {
            {
                super.bind(targetTypeToggleGroup.selectedToggleProperty(), courseUrl.textProperty(),
                        calendarUrl.textProperty(), constraintArea.textProperty());
            }

            @Override
            protected boolean computeValue() {
                if(targetTypeToggleGroup.getSelectedToggle() == singleButton &&
                        PackageVars.action == Action.CHECK_DATES) {
                    return (courseUrl.getText().isEmpty() || calendarUrl.getText().isEmpty());
                }

                if(targetTypeToggleGroup.getSelectedToggle() == singleButton) {
                    return courseUrl.getText().isEmpty();
                }

                if (targetTypeToggleGroup.getSelectedToggle() == termButton) {
                    return false;
                }

                if(targetTypeToggleGroup.getSelectedToggle() == constraintButton) {
                    return (constraintArea.getText().isEmpty());
                }

                return true;
            }
        };
        submitButton.disableProperty().bind(formFilledOut);
    }


    public void handleToggle(ActionEvent actionEvent) {
        setPaneVisibility(paneMap.get(targetTypeToggleGroup.getSelectedToggle()));
    }

    // Make specified pane visible and other panes invisible
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
        // Store params
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

        if(targetTypeToggleGroup.getSelectedToggle() == termButton) {
            String termCode = yearChoice.getSelectionModel().getSelectedItem().toString().substring(0, 4);
            switch (sessionChoice.getSelectionModel().getSelectedIndex()) {
                case 0: case 1: case 2: termCode += "10";
                    break;
                case 3: case 4: case 5: termCode += "20";
                    break;
                case 6: case 7: case 8: termCode += "30";
                    break;
            }

            String sessionCode = "";
            switch (sessionChoice.getSelectionModel().getSelectedIndex()) {
                case 0: case 3: case 6: sessionCode="OL";
                    break;
                case 1: case 4: case 7: sessionCode="OL-1";
                    break;
                case 2: case 5: case 8: sessionCode="OL-2";
                    break;
            }

            PackageVars.constraints = new ConstraintSet(termCode, sessionCode);
        }

        // Proceed to login, if action is TOGGLE_AVAILABILITY, SET_LANDING or REMOVE_ICONS, proceed to additionalParams
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        Parent root = null;

        if (PackageVars.action == Action.TOGGLE_AVAILABILITY || PackageVars.action == Action.SET_LANDING ||
                PackageVars.action == Action.REMOVE_ICONS || PackageVars.action == Action.REVSTAT){
            try {
                root = FXMLLoader.load(getClass().getResource("/view/additionalParams.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scene.setRoot(root);
    }
}
