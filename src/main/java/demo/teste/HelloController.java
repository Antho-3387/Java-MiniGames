package demo.teste;

import demo.teste.flappybird.FlappyBirdGameLauncher;
import demo.teste.moreless.MoreLessGameLauncher;
import demo.teste.snake.SnakeGameLauncher;
import demo.teste.snake.SnakeDifficulty;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Optional;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartGameClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String gameName = clickedButton.getText();

        switch (gameName) {
            case "Jeu du Snake" -> {
                Optional<SnakeDifficulty> difficulty = SnakeGameLauncher.openWindowWithDifficultyPrompt();
                if (difficulty.isPresent()) {
                    welcomeText.setText("Snake " + difficulty.get().getLabel().toLowerCase() + " lance. Bon jeu !");
                } else {
                    welcomeText.setText("Lancement du Snake annule.");
                }
            }
            case "Flappy Bird" -> {
                FlappyBirdGameLauncher.openWindow();
                welcomeText.setText("Flappy Bird lance. Bon jeu !");
            }
            case "Le Juste Prix" -> {
                MoreLessGameLauncher.launch();
                welcomeText.setText("Le Juste Prix lance. Bon jeu !");
            }
            case "Jeu du vrai ou faux" -> welcomeText.setText("Preparation du Quiz...");
            case "Jeu du Memory" -> welcomeText.setText("Mise en place des cartes...");
            default -> welcomeText.setText("Chargement du jeu...");
        }

        System.out.println("Action detectee : " + gameName);
    }

    @FXML
    protected void onQuitClick() {
        Platform.exit();
        System.exit(0);
    }
}