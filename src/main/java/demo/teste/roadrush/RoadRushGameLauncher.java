package demo.teste.roadrush;

import javafx.scene.Scene;
import javafx.stage.Stage;

public final class RoadRushGameLauncher {
    private RoadRushGameLauncher() {
    }

    public static void openWindow() {
        RoadRushGamePane gamePane = new RoadRushGamePane();
        Scene scene = new Scene(gamePane);

        Stage stage = new Stage();
        stage.setTitle("Road Rush");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();
        stage.setOnCloseRequest(event -> gamePane.stop());
        gamePane.requestFocus();
    }
}
