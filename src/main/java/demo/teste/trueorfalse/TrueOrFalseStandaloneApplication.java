package demo.teste.trueorfalse;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TrueOrFalseStandaloneApplication extends Application {
    @Override
    public void start(Stage stage) {
        TrueOrFalseGamePane gamePane = new TrueOrFalseGamePane();
        Scene scene = new Scene(gamePane, 640, 420);

        stage.setTitle("Vrai ou Faux");
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

