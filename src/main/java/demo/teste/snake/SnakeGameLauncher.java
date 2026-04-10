package demo.teste.snake;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SnakeGameLauncher {
    private SnakeGameLauncher() {
    }

    public static void openWindow() {
        SnakeGamePane gamePane = new SnakeGamePane();
        Scene scene = new Scene(gamePane);

        Stage stage = new Stage();
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();

        stage.setOnCloseRequest(event -> gamePane.stop());
        gamePane.requestFocus();
    }
}

