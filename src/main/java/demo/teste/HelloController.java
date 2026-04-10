package demo.teste;

import demo.teste.snake.SnakeGameLauncher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartGameClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String gameName = clickedButton.getText();

        switch (gameName) {
            case "Jeu du Snake" -> {
                welcomeText.setText("Snake lance. Bon jeu !");
                SnakeGameLauncher.openWindow();
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