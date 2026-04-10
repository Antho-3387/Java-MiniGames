package demo.teste.snake;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SnakeStandaloneApplication extends Application {
    @Override
    public void start(Stage stage) {
        SnakeGamePane gamePane = new SnakeGamePane();
        Scene scene = new Scene(gamePane);

        stage.setTitle("Snake Standalone");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();
        gamePane.requestFocus();

        stage.setOnCloseRequest(event -> gamePane.stop());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

