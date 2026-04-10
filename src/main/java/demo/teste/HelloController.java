package demo.teste;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.application.Platform;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onStartGameClick(ActionEvent event) {
        // On récupère le bouton qui a été cliqué
        Button clickedButton = (Button) event.getSource();
        String gameName = clickedButton.getText();

        // On change le texte en fonction du bouton
        switch (gameName) {
            case "Jeu du Snake":
                welcomeText.setText("Lancement du Snake...");
                // Appeler ta logique Snake ici
                break;
            case "Jeu du vrai ou faux":
                welcomeText.setText("Préparation du Quiz...");
                // Appeler ta logique Vrai/Faux ici
                break;
            case "Jeu du Memory":
                welcomeText.setText("Mise en place des cartes...");
                // Appeler ta logique Memory ici
                break;
            default:
                welcomeText.setText("Chargement du jeu...");
                break;
        }

        System.out.println("Action détectée : " + gameName);
    }

    @FXML
    protected void onQuitClick() {
        Platform.exit();
        System.exit(0);
    }
}