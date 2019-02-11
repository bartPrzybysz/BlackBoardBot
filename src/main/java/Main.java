import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage window) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/home.fxml"));
        window.setTitle("BlackBoardBot");
        window.setMinHeight(335);
        window.setMinWidth(550);
        window.setScene(new Scene(root, 600, 300));

        window.getIcons().add(new Image("/images/BotIcon.png"));
        window.show();
    }

    @Override
    public void stop() throws Exception {
        if (controllers.PackageVars.bot != null) {
            controllers.PackageVars.bot.stop();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
