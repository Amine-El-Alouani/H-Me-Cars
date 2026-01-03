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
                goToDashboard(event);
            } catch (IOException e) {
                e.printStackTrace();
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
}