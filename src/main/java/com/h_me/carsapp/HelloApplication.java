package com.h_me.carsapp;

import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HelloApplication extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Apply AtlantaFX Nord Dark theme as base
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        
        // Apply our custom stylesheet on top of AtlantaFX
        scene.getStylesheets().add(getClass().getResource("/com/h_me/carsapp/styles.css").toExternalForm());
        
        // Configure stage for fullscreen
        stage.setTitle("H-Me Cars");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        
        // Allow ESC to exit fullscreen but not close the app
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
        // Toggle fullscreen with F11
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        
        stage.show();
    }
    
    /**
     * Get the primary stage for global access.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}