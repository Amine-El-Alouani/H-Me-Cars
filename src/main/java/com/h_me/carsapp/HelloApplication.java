package com.h_me.carsapp; // <--- 1. CHANGE THIS to match the new folder

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dashboard.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("H-Me Cars");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}