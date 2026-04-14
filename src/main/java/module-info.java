module demo.teste {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens demo.teste to javafx.fxml;
    opens demo.teste.moreless to javafx.fxml;
    opens demo.teste.moreless.controller to javafx.fxml;
    opens demo.teste.moreless.model to javafx.base;

    exports demo.teste;
    exports demo.teste.snake;
    exports demo.teste.flappybird;
    exports demo.teste.trueorfalse;
    exports demo.teste.roadrush;
    exports demo.teste.moreless;
    exports demo.teste.moreless.controller;
    exports demo.teste.moreless.model;
    exports demo.teste.moreless.dao;
}