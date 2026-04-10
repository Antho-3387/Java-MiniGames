package demo.teste.snake;

import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.Optional;

public final class SnakeGameLauncher {
    private SnakeGameLauncher() {
    }

    public static void openWindow() {
        openWindow(SnakeDifficulty.HARD);
    }

    public static Optional<SnakeDifficulty> openWindowWithDifficultyPrompt() {
        ChoiceDialog<SnakeDifficulty> dialog = new ChoiceDialog<>(SnakeDifficulty.EASY, SnakeDifficulty.values());
        dialog.setTitle("Choix du niveau");
        dialog.setHeaderText("Selectionne la difficulte du Snake");
        dialog.setContentText("Niveau:");

        Optional<SnakeDifficulty> selected = dialog.showAndWait();
        selected.ifPresent(SnakeGameLauncher::openWindow);
        return selected;
    }

    public static void openWindow(SnakeDifficulty difficulty) {
        SnakeGamePane gamePane = new SnakeGamePane(difficulty);
        Scene scene = new Scene(gamePane);

        Stage stage = new Stage();
        stage.setTitle("Snake - " + difficulty.getLabel());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();

        stage.setOnCloseRequest(event -> gamePane.stop());
        gamePane.requestFocus();
    }
}

