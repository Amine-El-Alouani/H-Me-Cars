package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.ReservationDAO;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.model.Vehicle;
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
import java.util.List;

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
    // NEW COLUMNS
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
        // Use the new method with Details
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
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Car marked as " + newStatus);
                a.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Select a car to change status.");
            a.show();
        }
    }

    // ... handleAddCar, handleDeleteCar, handleLogout remain same as before ...
    @FXML
    public void handleAddCar() {
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
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void handleDeleteCar() {
        Vehicle selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            vehicleDAO.deleteVehicle(selected.getVehicleID());
            refreshData();
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