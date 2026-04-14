package demo.teste.moreless;

import javafx.application.Platform;
import javafx.stage.Stage;

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
                MoreLessStandaloneApplication app = new MoreLessStandaloneApplication();
                app.start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}