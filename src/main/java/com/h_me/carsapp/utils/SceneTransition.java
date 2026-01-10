package com.h_me.carsapp.utils;

import animatefx.animation.*;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Utility class for seamless scene transitions with animations.
 * Provides smooth fade and slide effects when navigating between views.
 */
public class SceneTransition {

    private static final Duration TRANSITION_DURATION = Duration.millis(400);

    /**
     * Transition to a new scene with a smooth fade effect.
     * Maintains fullscreen mode and consistent styling.
     */
    public static void switchScene(Node sourceNode, String fxmlPath, String title) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        
        // Load the new view
        FXMLLoader loader = new FXMLLoader(SceneTransition.class.getResource(fxmlPath));
        Parent newRoot = loader.load();
        
        // Get the current root for fade out
        Parent currentRoot = stage.getScene().getRoot();
        
        // Create a wrapper for the transition
        StackPane transitionPane = new StackPane();
        transitionPane.getChildren().addAll(newRoot, currentRoot);
        transitionPane.setStyle("-fx-background-color: #0a0e1a;");
        
        // Update the scene with the transition pane
        Scene scene = stage.getScene();
        scene.setRoot(transitionPane);
        
        // Fade out old, fade in new
        FadeTransition fadeOut = new FadeTransition(TRANSITION_DURATION, currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        FadeTransition fadeIn = new FadeTransition(TRANSITION_DURATION, newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Slight scale effect for depth
        ScaleTransition scaleIn = new ScaleTransition(TRANSITION_DURATION, newRoot);
        scaleIn.setFromX(0.98);
        scaleIn.setFromY(0.98);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        
        ParallelTransition transition = new ParallelTransition(fadeOut, fadeIn, scaleIn);
        transition.setOnFinished(e -> {
            scene.setRoot(newRoot);
            stage.setTitle(title);
        });
        
        transition.play();
    }

    /**
     * Quick fade transition for simpler navigation.
     */
    public static void fadeToScene(Node sourceNode, String fxmlPath, String title) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(SceneTransition.class.getResource(fxmlPath));
        Parent newRoot = loader.load();
        
        // Fade in animation using AnimateFX
        new FadeIn(newRoot).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(newRoot);
        stage.setTitle(title);
    }

    /**
     * Slide transition for going back.
     */
    public static void slideBack(Node sourceNode, String fxmlPath, String title) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(SceneTransition.class.getResource(fxmlPath));
        Parent newRoot = loader.load();
        
        new SlideInLeft(newRoot).setSpeed(2.0).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(newRoot);
        stage.setTitle(title);
    }

    /**
     * Slide transition for going forward.
     */
    public static void slideForward(Node sourceNode, String fxmlPath, String title) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(SceneTransition.class.getResource(fxmlPath));
        Parent newRoot = loader.load();
        
        new SlideInRight(newRoot).setSpeed(2.0).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(newRoot);
        stage.setTitle(title);
    }

    /**
     * Zoom in transition for important screens.
     */
    public static void zoomIn(Node sourceNode, String fxmlPath, String title) throws IOException {
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(SceneTransition.class.getResource(fxmlPath));
        Parent newRoot = loader.load();
        
        new ZoomIn(newRoot).setSpeed(1.8).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(newRoot);
        stage.setTitle(title);
    }
}
