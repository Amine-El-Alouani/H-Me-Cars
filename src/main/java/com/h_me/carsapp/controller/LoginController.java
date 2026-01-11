package com.h_me.carsapp.controller;

import animatefx.animation.FadeIn;
import com.h_me.carsapp.dao.UserDAO;
import com.h_me.carsapp.model.User;
import com.h_me.carsapp.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private VBox formContainer;
    @FXML private Button loginButton;

    private UserDAO userDAO = new UserDAO();
    private String originalButtonText;

    @FXML
    public void initialize() {
        // Clear any previous error
        errorLabel.setText("");
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        // Disable inputs during login
        setLoginInProgress(true);
        
        // Store event source for later use
        Node source = (Node) event.getSource();
        
        // Run authentication in background thread
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() {
                return userDAO.validateLogin(email, pass);
            }
        };
        
        loginTask.setOnSucceeded(e -> {
            User user = loginTask.getValue();
            Platform.runLater(() -> {
                if (user != null) {
                    UserSession.setSession(user);
                    try {
                        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                            goToAdminDashboard(source);
                        } else {
                            goToDashboard(source);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        showError("Error loading dashboard.");
                        setLoginInProgress(false);
                    }
                } else {
                    showError("Invalid email or password. Please try again.");
                    setLoginInProgress(false);
                }
            });
        });
        
        loginTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Connection error. Please try again.");
                setLoginInProgress(false);
            });
        });
        
        new Thread(loginTask).start();
    }
    
    private void setLoginInProgress(boolean inProgress) {
        emailField.setDisable(inProgress);
        passwordField.setDisable(inProgress);
        
        // Find the login button by traversing the scene
        if (loginButton != null) {
            if (inProgress) {
                originalButtonText = loginButton.getText();
                loginButton.setText("Signing in...");
                loginButton.setDisable(true);
            } else {
                loginButton.setText(originalButtonText != null ? originalButtonText : "Sign In");
                loginButton.setDisable(false);
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        // Add shake animation for error feedback
        new animatefx.animation.Shake(errorLabel).play();
    }

    @FXML
    public void goToRegister(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/register-view.fxml"));
        Parent root = loader.load();
        
        // Fade in animation
        new FadeIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Register - H-Me Cars");
    }

    private void goToDashboard(Node source) throws IOException {
        Stage stage = (Stage) source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dashboard.fxml"));
        Parent root = loader.load();
        
        // Zoom in animation for important transition
        new animatefx.animation.ZoomIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("H-Me Cars - Dashboard");
    }

    private void goToAdminDashboard(Node source) throws IOException {
        Stage stage = (Stage) source.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/admin-view.fxml"));
        Parent root = loader.load();
        
        // Zoom in animation for important transition
        new animatefx.animation.ZoomIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Admin Panel - H-Me Cars");
    }
}