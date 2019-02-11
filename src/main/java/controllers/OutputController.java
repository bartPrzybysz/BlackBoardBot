package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

public class OutputController {
    @FXML
    TextArea output;

    @FXML
    public void initialize() {
        output.appendText("Please do not interact with the chrome window until the process is finished.\n\n");

        // override print function to output to textArea
        System.setOut(new PrintStream(System.out) {
            @Override
            public void println(String s) {
                output.appendText(s + "\n");
            }

            @Override
            public void print(String s) {
                output.appendText(s);
            }
        });

        Runnable task;

        // assign appropriate function to task
        switch(PackageVars.action) {
            case REVSTAT:
                if (PackageVars.targetType == TargetType.SINGLE) {
                    task = () -> PackageVars.bot.revStat(PackageVars.courseUrl);
                } else {
                    task = () -> PackageVars.bot.revStat(PackageVars.constraints);
                }
                break;
            case TITLE_COLOR:
                if (PackageVars.targetType == TargetType.SINGLE) {
                    task = () -> PackageVars.bot.titleColor(PackageVars.courseUrl);
                } else {
                    task = () -> PackageVars.bot.titleColor(PackageVars.constraints);
                }
                break;
            case REMOVE_ICONS:
                if (PackageVars.targetType == TargetType.SINGLE) {
                    task = () -> PackageVars.bot.removeIcons(PackageVars.courseUrl);
                } else {
                    task = () -> PackageVars.bot.removeIcons(PackageVars.constraints);
                }
                break;
            case TOGGLE_AVAILABILITY:
                task = () -> PackageVars.bot.toggleAvailability(PackageVars.constraints, PackageVars.availability);
                break;
            case SET_LANDING:
                task = () -> PackageVars.bot.setLanding(PackageVars.constraints, PackageVars.landingPage);
                break;
            case CHECK_DATES:
                task = () -> PackageVars.bot.checkDates(PackageVars.courseUrl, PackageVars.calendarUrl);
                break;
            default:
                task = () -> System.out.println("No task defined");
        }

        Thread taskThread = new Thread(task);
        taskThread.start();
    }

    public void handleHome(ActionEvent actionEvent) {
        if(PackageVars.bot.isRunning()) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Are you sure?");
            confirmAlert.setHeaderText("Returning home will cause the process to stop");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.get() == ButtonType.OK){
                PackageVars.bot.stop();
            } else {
                return;
            }
        }

        //Switch to home scene
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        window.setScene(new Scene(root, 600, 300));
    }

    public void handleStop(ActionEvent actionEvent) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Are you sure?");
        confirmAlert.setHeaderText("Are you sure you want to interrupt this process?");

        if(PackageVars.bot.isRunning()) {
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                PackageVars.bot.stop();
            }
        }
    }
}
