module demo.teste {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens demo.teste to javafx.fxml;
    exports demo.teste;
    exports demo.teste.snake;
    exports demo.teste.flappybird;
    exports demo.teste.trueorfalse;
    exports demo.teste.roadrush;
}