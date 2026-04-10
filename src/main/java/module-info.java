module demo.teste {
    requires javafx.controls;
    requires javafx.fxml;


    opens demo.teste to javafx.fxml;
    exports demo.teste;
    exports demo.teste.snake;
}