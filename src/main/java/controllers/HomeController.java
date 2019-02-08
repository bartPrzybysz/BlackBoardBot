package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

public class HomeController {
    @FXML
    ToggleGroup actionSelectToggleGroup;

    public void myGroupAction(ActionEvent action)
    {
        System.out.println("Toggled: " + actionSelectToggleGroup.getSelectedToggle().getUserData().toString());
    }

    public void handleSubmit(ActionEvent actionEvent) {
        System.out.println("TODO");
    }
}
