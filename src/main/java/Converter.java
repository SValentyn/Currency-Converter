import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main class of the program where the launch of the JavaFX application runs and the properties are set.
 *
 * @author Syniuk Valentyn
 * @version 1.0
 */
public class Converter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("form.fxml"));
        root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.getIcons().add(new Image(getClass().getResource("/img/icon.png").toString()));
        stage.setScene(new Scene(root, 400, 540));
        stage.setTitle("Currency-Converter");
        stage.setResizable(false);
        stage.setX(50);
        stage.setY(50);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
