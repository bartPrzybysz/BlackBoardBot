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
        if(bot != null) {
            userName.setText(bot.username());
            password.setText(bot.password());
        }

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
        try {
            root = FXMLLoader.load(getClass().getResource("/view/target.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
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
    }
}
