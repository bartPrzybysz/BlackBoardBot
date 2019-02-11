package controllers;

import blackboardbot.BBB;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static controllers.PackageVars.bot;

public class LoginController {
    @FXML
    TextField userName;

    @FXML
    PasswordField password;

    @FXML
    Button submitButton;

    @FXML
    public void initialize() {
        // if username and password already specified, display them
        if(bot != null) {
            userName.setText(bot.username());
            password.setText(bot.password());
        }

        // Disable submit button until form filled out
        BooleanBinding formFilledOut = new BooleanBinding() {
            {
                super.bind(userName.textProperty(), password.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return userName.getText().isEmpty() || password.getText().isEmpty();
            }
        };
        submitButton.disableProperty().bind(formFilledOut);
    }

    public void handleBack(ActionEvent actionEvent) {
        Scene scene = ((Node) actionEvent.getSource()).getScene();
        Parent root = null;

        if (PackageVars.action == Action.TOGGLE_AVAILABILITY || PackageVars.action == Action.SET_LANDING){
            try {
                root = FXMLLoader.load(getClass().getResource("/view/additionalParams.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(getClass().getResource("/view/target.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scene.setRoot(root);
    }

    public void handleSubmit(ActionEvent actionEvent) {
        if(PackageVars.bot == null) {
            PackageVars.bot = new BBB(userName.getText(), password.getText());
        } else {
            PackageVars.bot.username(userName.getText());
            PackageVars.bot.password(password.getText());
        }

        // Switch to output scene
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/output.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        window.setScene(new Scene(root, 600, 400));
    }
}
