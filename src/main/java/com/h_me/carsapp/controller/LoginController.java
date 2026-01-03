package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.UserDAO;
import com.h_me.carsapp.model.User;
import com.h_me.carsapp.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String pass = passwordField.getText();

        User user = userDAO.validateLogin(email, pass);

        if (user != null) {
            UserSession.setSession(user);

            try {
                // --- NEW LOGIC: Check Role and Redirect ---
                // Ensure your User model has the .getRole() method!
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    goToAdminDashboard(event);
                } else {
                    goToDashboard(event);
                }

            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error loading dashboard.");
            }
        } else {
            errorLabel.setText("Invalid Email or Password");
        }
    }

    @FXML
    public void goToRegister(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/register-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Register - H-Me Cars");
        stage.setScene(scene);
        stage.show();
    }

    private void goToDashboard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("H-Me Cars Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    // --- NEW METHOD: Load Admin View ---
    private void goToAdminDashboard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/admin-view.fxml"));
        // Admin view might need a slightly larger window
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Admin Panel - H-Me Cars");
        stage.setScene(scene);
        stage.show();
    }
}