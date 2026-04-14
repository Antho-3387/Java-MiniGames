package demo.teste.moreless;

import demo.teste.moreless.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MoreLessStandaloneApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseManager.init();

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/demo/teste/moreless/fxml/game.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720);

        stage.setTitle("Le Juste Prix");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}