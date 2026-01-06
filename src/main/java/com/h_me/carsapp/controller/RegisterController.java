package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.UserDAO;
import com.h_me.carsapp.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
        if(emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert("Error", "Please fill all fields");
            return;
        }

        try {
            User u = new User();
            int randomId = 100000 + new Random().nextInt(900000);
            u.setUserID(String.valueOf(randomId)); // Set as String for User Model

            u.setFirstName(firstNameField.getText());
            u.setLastName(lastNameField.getText());
            u.setPhoneNum(Integer.parseInt(phoneField.getText()));
            u.setEmail(emailField.getText());
            u.setPassword(passwordField.getText());

            if(userDAO.registerUser(u)) {
                showAlert("Success", "Account created! Please Login.");
                goToLogin(event);
            } else {
                showAlert("Error", "Registration failed (Email might exist).");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Phone number must be numeric.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Login - H-Me Cars");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}