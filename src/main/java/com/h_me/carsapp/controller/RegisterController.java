package com.h_me.carsapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.Shake;
import com.h_me.carsapp.dao.UserDAO;
import com.h_me.carsapp.model.User;
import com.h_me.carsapp.utils.StyledAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void handleRegister(ActionEvent event) {
        // Validate all fields
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            StyledAlert.warning("Missing Information", "Please enter your first and last name.");
            shakeField(firstNameField);
            shakeField(lastNameField);
            return;
        }

        if (phoneField.getText().isEmpty()) {
            StyledAlert.warning("Missing Information", "Please enter your phone number.");
            shakeField(phoneField);
            return;
        }

        if (emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            StyledAlert.warning("Missing Information", "Please enter your email and password.");
            shakeField(emailField);
            shakeField(passwordField);
            return;
        }

        try {
            User u = new User();
            int randomId = 100000 + new Random().nextInt(900000);
            u.setUserID(String.valueOf(randomId));

            u.setFirstName(firstNameField.getText().trim());
            u.setLastName(lastNameField.getText().trim());
            u.setPhoneNum(Integer.parseInt(phoneField.getText().trim()));
            u.setEmail(emailField.getText().trim());
            u.setPassword(passwordField.getText());

            if (userDAO.registerUser(u)) {
                StyledAlert.success("Account Created!", "Your account has been created successfully. Please sign in to continue.");
                goToLogin(event);
            } else {
                StyledAlert.error("Registration Failed", "Could not create account. This email may already be registered.");
            }
        } catch (NumberFormatException e) {
            StyledAlert.error("Invalid Phone", "Phone number must contain only digits.");
            shakeField(phoneField);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void shakeField(TextField field) {
        new Shake(field).play();
    }

    @FXML
    public void goToLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Parent root = loader.load();
        
        // Fade in animation
        new FadeIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Login - H-Me Cars");
    }
}