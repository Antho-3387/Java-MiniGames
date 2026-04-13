package demo.teste.moreless;

import demo.teste.snake.SnakeGamePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class moreLess extends Application {

    @Override
    public void start(Stage stage) {
        SnakeGamePane gamePane = new SnakeGamePane();
        Scene scene = new Scene(gamePane);

        stage.setTitle("More OR Less ?");
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
