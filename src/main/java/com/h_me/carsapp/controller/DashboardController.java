package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.service.ReservationService;
import com.h_me.carsapp.utils.UserSession; // Import Session
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DashboardController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> colName;
    @FXML private TableColumn<Vehicle, String> colCategory;
    @FXML private TableColumn<Vehicle, Double> colPrice;
    @FXML private TableColumn<Vehicle, String> colStatus;
    @FXML private TextField searchField;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private VehicleDAO vehicleDAO;
    private ReservationService reservationService;
    private ObservableList<Vehicle> vehicleList;

    @FXML
    public void initialize() {
        vehicleDAO = new VehicleDAO();
        reservationService = new ReservationService();

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("priceRental"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadData();
    }

    @FXML
    public void loadData() {
        List<Vehicle> data = vehicleDAO.getAllAvailableVehicles();
        vehicleList = FXCollections.observableArrayList(data);
        vehicleTable.setItems(vehicleList);
    }

    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();
        ObservableList<Vehicle> filteredList = vehicleList.filtered(v ->
                v.getName().toLowerCase().contains(query) ||
                        v.getCategory().toLowerCase().contains(query)
        );
        vehicleTable.setItems(filteredList);
    }

    @FXML
    public void handleRent() {
        Vehicle selectedCar = vehicleTable.getSelectionModel().getSelectedItem();

        if (selectedCar == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a car to rent.");
            return;
        }

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Dates", "Please select Start and End dates.");
            return;
        }

        LocalDateTime start = startDatePicker.getValue().atStartOfDay();
        LocalDateTime end = endDatePicker.getValue().atStartOfDay();

        if (end.isBefore(start)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Dates", "End date cannot be before start date.");
            return;
        }

        long days = ChronoUnit.DAYS.between(start, end);
        if (days < 1) days = 1;
        double estimatedCost = days * selectedCar.getPriceRental();

        // --- USER ID CHECK ---
        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert(Alert.AlertType.ERROR, "Not Logged In", "Please log out and log in again.");
            return;
        }

        try {
            int userId = Integer.parseInt(session.getUser().getUserID());

            // Call Service
            boolean success = reservationService.processRental(selectedCar, userId, start, end);

            if (success) {
                String msg = String.format("Car: %s\nDays: %d\nTotal: %.2f MAD", selectedCar.getName(), days, estimatedCost);
                showAlert(Alert.AlertType.INFORMATION, "Reservation Confirmed!", msg);
                loadData(); // Refresh table to remove rented car
            } else {
                showAlert(Alert.AlertType.ERROR, "Rental Failed", "Check the Console for Database Errors.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "User ID Error", "Your User ID is not a number. Admin accounts cannot rent cars.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", e.getMessage());
        }
    }

    // --- NEW LOGOUT METHOD ---
    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        // 1. Clear Session
        UserSession.cleanUserSession();

        // 2. Go to Login View
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Login - H-Me Cars");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goToDealerships(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dealership-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}