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
        window.setTitle("Black Board Bot");
        window.setMinHeight(335);
        window.setMinWidth(500);
        window.setScene(new Scene(root, 600, 300));

        window.getIcons().add(new Image("/images/BotIcon.png"));
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
