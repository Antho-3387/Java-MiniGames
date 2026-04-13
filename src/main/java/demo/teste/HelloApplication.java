package demo.teste;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                Objects.requireNonNull(HelloApplication.class.getResource("hello-view.fxml"))
        );
        Scene scene = new Scene(fxmlLoader.load(), 420, 380);
        stage.setTitle("MiniGames");
        stage.setScene(scene);
        stage.show();
    }
}
