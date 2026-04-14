package demo.teste.trueorfalse;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class TrueOrFalseGameLauncher {
    private TrueOrFalseGameLauncher() {
    }

    public static void openWindow() {
        TrueOrFalseGamePane gamePane = new TrueOrFalseGamePane();
        Scene scene = new Scene(gamePane, 640, 420);

        Stage stage = new Stage();
        stage.setTitle("Vrai ou Faux");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();
        gamePane.requestFocus();

        stage.setOnCloseRequest(event -> gamePane.stop());
    }
}
