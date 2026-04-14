package demo.teste.roadrush;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RoadRushStandaloneApplication extends Application {
    @Override
    public void start(Stage stage) {
        RoadRushGamePane gamePane = new RoadRushGamePane();
        Scene scene = new Scene(gamePane, 640, 420);

        stage.setTitle("Road Rush");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gamePane.bindScene(scene);
        gamePane.start();
        gamePane.requestFocus();

        stage.setOnCloseRequest(event -> gamePane.stop());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

