package demo.teste.moreless;

import demo.teste.moreless.dao.DatabaseManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public final class MoreLessGameLauncher {

    private MoreLessGameLauncher() {}

    public static void main(String[] args) {
        MoreLessStandaloneApplication.main(args);
    }

    /**
     * Ouvre le jeu dans une nouvelle fenêtre depuis un contexte JavaFX déjà démarré
     * (ex. : appel depuis HelloController).
     */
    public static void launch() {
        Platform.runLater(() -> {
            try {
                DatabaseManager.init();

                URL fxmlUrl = MoreLessGameLauncher.class.getResource("/demo/teste/moreless/fxml/game.fxml");
                if (fxmlUrl == null) {
                    System.err.println("[MoreLess] FXML introuvable: /demo/teste/moreless/fxml/game.fxml");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Le Juste Prix");
                stage.setScene(new Scene(root, 1280, 720));
                stage.setResizable(false);
                stage.show();
            } catch (Exception e) {
                System.err.println("[MoreLess] Erreur d'ouverture de la fenetre: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}