package com.h_me.carsapp.controller;

import animatefx.animation.FadeIn;
import com.h_me.carsapp.dao.UserDAO;
import com.h_me.carsapp.model.User;
import com.h_me.carsapp.utils.SceneTransition;
import com.h_me.carsapp.utils.UserSession;
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

    private UserDAO userDAO = new UserDAO();

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

        User user = userDAO.validateLogin(email, pass);

        if (user != null) {
            UserSession.setSession(user);

            try {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    goToAdminDashboard(event);
                } else {
                    goToDashboard(event);
                }

            } catch (IOException e) {
                e.printStackTrace();
                showError("Error loading dashboard.");
            }
        } else {
            showError("Invalid email or password. Please try again.");
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

    private void goToDashboard(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dashboard.fxml"));
        Parent root = loader.load();
        
        // Zoom in animation for important transition
        new animatefx.animation.ZoomIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("H-Me Cars - Dashboard");
    }

    private void goToAdminDashboard(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/admin-view.fxml"));
        Parent root = loader.load();
        
        // Zoom in animation for important transition
        new animatefx.animation.ZoomIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Admin Panel - H-Me Cars");
    }
}