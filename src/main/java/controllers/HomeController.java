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
import java.util.HashSet;

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

    private class ActionMap {
        private HashSet<ActionObject> actionObjects = new HashSet<>();

        private class ActionObject {
            Action action;
            ToggleButton toggleButton;
            Pane pane;

            ActionObject(Action action, ToggleButton toggleButton, Pane pane) {
                this.action = action;
                this.toggleButton = toggleButton;
                this.pane = pane;
            }
        }

        void put(Action action, ToggleButton toggleButton, Pane pane) {
            actionObjects.add(new ActionObject(action, toggleButton, pane));
        }

        Action getAction(ToggleButton toggleButton) {
            for(ActionObject a : actionObjects) {
                if (a.toggleButton == toggleButton) {
                    return a.action;
                }
            }
            return null;
        }
        Action getAction(Pane pane) {
            for(ActionObject a : actionObjects) {
                if (a.pane == pane) {
                    return a.action;
                }
            }
            return null;
        }

        ToggleButton getToggleButton(Action action) {
            for(ActionObject a : actionObjects) {
                if (a.action == action) {
                    return a.toggleButton;
                }
            }
            return null;
        }
        ToggleButton getToggleButton(Pane pane) {
            for(ActionObject a : actionObjects) {
                if (a.pane == pane) {
                    return a.toggleButton;
                }
            }
            return null;
        }

        Pane getPane(Action action) {
            for(ActionObject a : actionObjects) {
                if (a.action == action) {
                    return a.pane;
                }
            }
            return null;
        }
        Pane getPane(ToggleButton toggleButton) {
            for(ActionObject a : actionObjects) {
                if(a.toggleButton == toggleButton) {
                    return a.pane;
                }
            }
            return null;
        }
    }
    private ActionMap actionMap = new ActionMap();

    @FXML
    public void initialize() {
        actionMap.put(Action.REVSTAT, revstatButton, revstatPage);
        actionMap.put(Action.TITLE_COLOR, titleColorButton, titleColorPage);
        actionMap.put(Action.REMOVE_ICONS, removeIconsButton, removeIconsPage);
        actionMap.put(Action.TOGGLE_AVAILABILITY, toggleAvailabilityButton, toggleAvailabilityPage);
        actionMap.put(Action.SET_LANDING, setLandingButton, setLandingPage);
        actionMap.put(Action.CHECK_DATES, checkDatesButton, checkDatesPage);

        if (PackageVars.action != null) {
            actionMap.getToggleButton(PackageVars.action).setSelected(true);
            setPaneVisibility(actionMap.getPane(PackageVars.action));
            submitButton.setDisable(false);
        }
    }

    @FXML
    public void handleToggle(ActionEvent actionEvent) {
        if (actionToggleGroup.getSelectedToggle() == null) {
            submitButton.setDisable(true);
            setPaneVisibility(introPage);
        } else {
            submitButton.setDisable(false);
            setPaneVisibility(actionMap.getPane((ToggleButton) actionToggleGroup.getSelectedToggle()));
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
        PackageVars.action = actionMap.getAction((ToggleButton) actionToggleGroup.getSelectedToggle());

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
