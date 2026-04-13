package demo.teste.flappybird;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class FlappyBirdGameLauncher {
    private FlappyBirdGameLauncher() {
    }

    public static void openWindow() {
        FlappyBirdGamePane gamePane = new FlappyBirdGamePane();
        Scene scene = new Scene(gamePane);

        Stage stage = new Stage();
        stage.setTitle("Flappy Bird");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();
        stage.setOnCloseRequest(event -> gamePane.stop());
        gamePane.requestFocus();
    }
}

