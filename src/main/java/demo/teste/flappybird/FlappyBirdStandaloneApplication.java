package demo.teste.flappybird;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FlappyBirdStandaloneApplication extends Application {
    @Override
    public void start(Stage stage) {
        FlappyBirdGamePane gamePane = new FlappyBirdGamePane();
        Scene scene = new Scene(gamePane);

        stage.setTitle("Flappy Bird");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();
        stage.setOnCloseRequest(event -> gamePane.stop());
        gamePane.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

