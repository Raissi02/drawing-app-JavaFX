module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens app to javafx.fxml;
    opens app.controller to javafx.fxml;
    opens app.model to javafx.base;

    exports app;
    exports app.controller;
    exports app.model;
    exports app.dao;
    exports app.factory;
    exports app.command;
    exports app.strategy;
    exports app.singleton;
    exports app.util;
}
