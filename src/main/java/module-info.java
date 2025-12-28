module com.example.hmecars {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;



    requires java.sql;
    requires org.postgresql.jdbc;
    opens com.h_me.carsapp to javafx.fxml;
    opens com.h_me.carsapp.controller to javafx.fxml;
    opens com.h_me.carsapp.model to javafx.base;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;

    opens com.example.hmecars to javafx.fxml;
    exports com.example.hmecars;
    exports com.h_me.carsapp;
}