module com.example.hmeflights {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;


    requires org.neo4j.driver;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;

    opens com.example.hmeflights to javafx.fxml;
    exports com.example.hmeflights;
}