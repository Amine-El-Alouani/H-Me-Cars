package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.ReservationDAO;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.utils.StyledAlert;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {

    // --- CAR TAB ---
    @FXML private TableView<Vehicle> carTable;
    @FXML private TableColumn<Vehicle, Integer> colCarId;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, String> colStatus;

    @FXML private TextField modelField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;

    // --- RESERVATION TAB ---
    @FXML private TableView<Reservation> resTable;
    @FXML private TableColumn<Reservation, Integer> colResId;
    @FXML private TableColumn<Reservation, Integer> colResCarId;
    @FXML private TableColumn<Reservation, Integer> colCost;
    @FXML private TableColumn<Reservation, String> colUserName;
    @FXML private TableColumn<Reservation, String> colUserPhone;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        // Car Columns
        colCarId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Reservation Columns
        colResId.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colResCarId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Bind new columns to the fields we added in Reservation.java
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colUserPhone.setCellValueFactory(new PropertyValueFactory<>("userPhone"));

        refreshData();
    }

    @FXML
    public void refreshData() {
        carTable.setItems(FXCollections.observableArrayList(vehicleDAO.getAllAvailableVehicles()));
        resTable.setItems(FXCollections.observableArrayList(reservationDAO.getAllReservationsWithDetails()));
    }

    @FXML
    public void handleToggleStatus() {
        Vehicle selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String current = selected.getStatus();
            String newStatus = current.equalsIgnoreCase("AVAILABLE") ? "MAINTENANCE" : "AVAILABLE";

            try {
                vehicleDAO.updateVehicleStatus(selected.getVehicleID(), newStatus);
                refreshData(); // Refresh to see change
                StyledAlert.success("Status Updated", "Car has been marked as " + newStatus);
            } catch (Exception e) {
                e.printStackTrace();
                StyledAlert.error("Update Failed", "Could not update car status.");
            }
        } else {
            StyledAlert.warning("No Selection", "Please select a car to change its status.");
        }
    }

    @FXML
    public void handleAddCar() {
        if (modelField.getText().isEmpty() || categoryField.getText().isEmpty() || priceField.getText().isEmpty()) {
            StyledAlert.warning("Missing Fields", "Please fill in Model, Category, and Price to add a car.");
            return;
        }
        
        try {
            Vehicle v = new Vehicle();
            v.setName(modelField.getText());
            v.setCategory(categoryField.getText());
            v.setPriceRental(Double.parseDouble(priceField.getText()));
            v.setPricePurchase(0); v.setDealershipID(1); v.setManufactureID(1);
            v.setStatus("AVAILABLE");
            vehicleDAO.addVehicle(v);
            refreshData();
            clearFields();
            StyledAlert.success("Car Added", "New car \"" + v.getName() + "\" has been added to inventory.");
        } catch (NumberFormatException e) {
            StyledAlert.error("Invalid Price", "Price must be a valid number.");
        } catch (Exception e) { 
            e.printStackTrace(); 
            StyledAlert.error("Add Failed", "Could not add car to database.");
        }
    }

    @FXML
    public void handleDeleteCar() {
        Vehicle selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            vehicleDAO.deleteVehicle(selected.getVehicleID());
            refreshData();
            StyledAlert.success("Car Deleted", "\"" + selected.getName() + "\" has been removed from inventory.");
        } else {
            StyledAlert.warning("No Selection", "Please select a car to delete.");
        }
    }

    private void clearFields() { modelField.clear(); categoryField.clear(); priceField.clear(); }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}