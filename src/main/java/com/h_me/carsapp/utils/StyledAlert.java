package com.h_me.carsapp.utils;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Custom styled alert popups that match the app's premium dark theme.
 * Replaces standard JavaFX Alert with animated, modern dialogs.
 */
public class StyledAlert {

    public enum AlertType {
        INFO, SUCCESS, WARNING, ERROR
    }

    private static final String[] INFO_COLORS = {"#6366f1", "#8b5cf6"};
    private static final String[] SUCCESS_COLORS = {"#10b981", "#059669"};
    private static final String[] WARNING_COLORS = {"#f59e0b", "#d97706"};
    private static final String[] ERROR_COLORS = {"#f43f5e", "#e11d48"};

    private static final String[] INFO_ICONS = {"ℹ️", "Information"};
    private static final String[] SUCCESS_ICONS = {"✅", "Success"};
    private static final String[] WARNING_ICONS = {"⚠️", "Warning"};
    private static final String[] ERROR_ICONS = {"❌", "Error"};

    /**
     * Shows a styled alert dialog with animation.
     */
    public static void show(AlertType type, String title, String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        String[] colors = getColors(type);
        String[] icons = getIcons(type);

        // Main container
        VBox container = new VBox();
        container.setStyle("""
            -fx-background-color: linear-gradient(to bottom, #1e293b, #172033);
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.1);
            -fx-border-width: 1;
            """);
        container.setEffect(new DropShadow(40, Color.rgb(0, 0, 0, 0.6)));
        container.setMinWidth(380);
        container.setMaxWidth(420);

        // Header with icon and type
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20, 24, 16, 24));
        header.setStyle(String.format("""
            -fx-background-color: linear-gradient(to right, %s, %s);
            -fx-background-radius: 20 20 0 0;
            """, colors[0], colors[1]));

        Label iconLabel = new Label(icons[0]);
        iconLabel.setStyle("-fx-font-size: 28px;");

        Label typeLabel = new Label(icons[1]);
        typeLabel.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-opacity: 0.9;
            """);

        header.getChildren().addAll(iconLabel, typeLabel);

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle("""
            -fx-text-fill: #f8fafc;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            """);
        titleLabel.setWrapText(true);
        titleLabel.setPadding(new Insets(20, 24, 8, 24));

        // Message
        Label messageLabel = new Label(message);
        messageLabel.setStyle("""
            -fx-text-fill: #94a3b8;
            -fx-font-size: 14px;
            -fx-line-spacing: 4;
            """);
        messageLabel.setWrapText(true);
        messageLabel.setPadding(new Insets(0, 24, 24, 24));
        messageLabel.setMaxWidth(370);

        // Button container
        HBox buttonContainer = new HBox();
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setPadding(new Insets(0, 24, 20, 24));

        Button okButton = new Button("Got it");
        okButton.setStyle(String.format("""
            -fx-background-color: linear-gradient(to right, %s, %s);
            -fx-text-fill: white;
            -fx-background-radius: 12;
            -fx-padding: 12 32;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-cursor: hand;
            """, colors[0], colors[1]));
        
        okButton.setOnMouseEntered(e -> {
            okButton.setStyle(String.format("""
                -fx-background-color: linear-gradient(to right, %s, %s);
                -fx-text-fill: white;
                -fx-background-radius: 12;
                -fx-padding: 12 32;
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-cursor: hand;
                -fx-translate-y: -2;
                """, colors[1], colors[0]));
        });
        
        okButton.setOnMouseExited(e -> {
            okButton.setStyle(String.format("""
                -fx-background-color: linear-gradient(to right, %s, %s);
                -fx-text-fill: white;
                -fx-background-radius: 12;
                -fx-padding: 12 32;
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-cursor: hand;
                """, colors[0], colors[1]));
        });
        
        okButton.setOnAction(e -> {
            // Fade out animation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), container);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> dialog.close());
            fadeOut.play();
        });

        buttonContainer.getChildren().add(okButton);

        container.getChildren().addAll(header, titleLabel, messageLabel, buttonContainer);

        Scene scene = new Scene(container);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);

        // Entrance animation
        container.setScaleX(0.8);
        container.setScaleY(0.8);
        container.setOpacity(0);

        dialog.show();

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), container);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), container);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        scaleIn.play();
        fadeIn.play();
    }

    /**
     * Convenience method for info alerts.
     */
    public static void info(String title, String message) {
        show(AlertType.INFO, title, message);
    }

    /**
     * Convenience method for success alerts.
     */
    public static void success(String title, String message) {
        show(AlertType.SUCCESS, title, message);
    }

    /**
     * Convenience method for warning alerts.
     */
    public static void warning(String title, String message) {
        show(AlertType.WARNING, title, message);
    }

    /**
     * Convenience method for error alerts.
     */
    public static void error(String title, String message) {
        show(AlertType.ERROR, title, message);
    }

    private static String[] getColors(AlertType type) {
        return switch (type) {
            case INFO -> INFO_COLORS;
            case SUCCESS -> SUCCESS_COLORS;
            case WARNING -> WARNING_COLORS;
            case ERROR -> ERROR_COLORS;
        };
    }

    private static String[] getIcons(AlertType type) {
        return switch (type) {
            case INFO -> INFO_ICONS;
            case SUCCESS -> SUCCESS_ICONS;
            case WARNING -> WARNING_ICONS;
            case ERROR -> ERROR_ICONS;
        };
    }
}
