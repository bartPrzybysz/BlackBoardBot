package controllers;

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

public class AdditionalParamsController {
    @FXML
    ToggleGroup landingToggleGroup, selectAction;

    @FXML
    ToggleButton predefButton, customButton, toggleOn, toggleOff;

    @FXML
    Pane landingPageInput, toggleCourseInput, choiceInput, choiceText;

    @FXML
    ChoiceBox landingPageChoice;

    @FXML
    TextField landingPageText;

    @FXML
    Button submitButton;

    @FXML
    public void initialize() {
        // If params already set, display them
        if (PackageVars.availability != null) {
            if(PackageVars.availability.equals("ON")) {
                toggleOn.setSelected(true);
            } else {
                toggleOn.setSelected(false);
            }
        }
        landingPageChoice.getSelectionModel().selectFirst();
        if (PackageVars.landingPage != null) {
            landingPageText.setText(PackageVars.landingPage);
        }

        if (PackageVars.action == Action.SET_LANDING) {
            landingPageInput.setVisible(true);
            predefButton.setSelected(true);
            handleToggle(null);

        } else {
            toggleCourseInput.setVisible(true);
        }

        //Disable submit button until form filled out
        BooleanBinding formFilledOut = new BooleanBinding() {
            {
                super.bind(selectAction.selectedToggleProperty(), landingToggleGroup.selectedToggleProperty(),
                        landingPageText.textProperty());
            }

            @Override
            protected boolean computeValue() {
                if (PackageVars.action == Action.TOGGLE_AVAILABILITY) {
                    return selectAction.getSelectedToggle() == null;
                } else if (PackageVars.action == Action.SET_LANDING) {
                    if (landingToggleGroup.getSelectedToggle() == null) {
                        return true;
                    } else if (landingToggleGroup.getSelectedToggle() == customButton) {
                        return landingPageText.getText().isEmpty();
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        };
        submitButton.disableProperty().bind(formFilledOut);
    }

    // Toggle landing page select pane
    public void handleToggle(ActionEvent actionEvent) {
        if (landingToggleGroup.getSelectedToggle() == predefButton) {
            choiceInput.setVisible(true);
            choiceInput.setManaged(true);
            choiceText.setVisible(false);
            choiceText.setManaged(false);
        } else if (landingToggleGroup.getSelectedToggle() == customButton) {
            choiceInput.setVisible(false);
            choiceInput.setManaged(false);
            choiceText.setVisible(true);
            choiceText.setManaged(true);
        }
    }

    public void handleBack(ActionEvent actionEvent) {
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/target.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene.setRoot(root);
    }

    public void handleSubmit(ActionEvent actionEvent) {
        // store params
        if (selectAction.getSelectedToggle() == toggleOn) {
            PackageVars.availability = "ON";
        }
        if (selectAction.getSelectedToggle() == toggleOff) {
            PackageVars.availability = "OFF";
        }

        if (landingToggleGroup.getSelectedToggle() == predefButton) {
            PackageVars.landingPage = landingPageChoice.getSelectionModel().getSelectedItem().toString();
        } else {
            PackageVars.landingPage = landingPageText.getText();
        }

        // proceed to login
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scene.setRoot(root);
    }
}
