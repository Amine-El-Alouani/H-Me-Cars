package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.service.RentalService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import javafx.scene.control.Alert;

import java.time.LocalDateTime;
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
    private RentalService rentalService;
    private ObservableList<Vehicle> vehicleList;

    @FXML
    public void initialize() {
        vehicleDAO = new VehicleDAO();
        rentalService = new RentalService();

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
            showAlert("Error", "No car selected!", "Please click on a car in the table.");
            return;
        }

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("Error", "Missing Dates", "Please select both start and end dates.");
            return;
        }

        LocalDateTime start = startDatePicker.getValue().atStartOfDay();
        LocalDateTime end = endDatePicker.getValue().atStartOfDay();

        if (end.isBefore(start)) {
            showAlert("Error", "Invalid Dates", "End date cannot be before start date.");
            return;
        }

        long days = ChronoUnit.DAYS.between(start, end);
        if (days < 1) days = 1; // Minimum 1 day
        double estimatedCost = days * selectedCar.getPriceRental();

        int fakeUserId = 1;
        boolean success = rentalService.processRental(selectedCar, fakeUserId, start, end);

        if (success) {
            String message = String.format(
                    "Car: %s\nDuration: %d Days\nTotal Price: %.2f MAD",
                    selectedCar.getName(),
                    days,
                    estimatedCost
            );

            showAlert("Rental Successful", "Reservation Confirmed!", message);

            loadData();
        } else {
            showAlert("Error", "Rental Failed", "Could not process transaction. The car might be unavailable.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void goToDealerships(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dealership-view.fxml"));
        javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load(), 900, 600);
        javafx.stage.Stage stage = (javafx.stage.Stage)((javafx.scene.Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}

