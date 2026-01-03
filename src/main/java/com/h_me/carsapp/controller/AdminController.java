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
    @FXML private TableColumn<Reservation, Integer> colUserId;
    @FXML private TableColumn<Reservation, Integer> colResCarId;
    @FXML private TableColumn<Reservation, Integer> colCost;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();

    @FXML
    public void initialize() {
        // Setup Car Columns
        colCarId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup Reservation Columns
        colResId.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colResCarId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        refreshData();
    }

    private void refreshData() {
        carTable.setItems(FXCollections.observableArrayList(vehicleDAO.getAllAvailableVehicles()));
        // Note: For getAllAvailableVehicles, you might want to create 'getAllVehicles' in DAO if you want to see rented ones too.
        resTable.setItems(FXCollections.observableArrayList(reservationDAO.getAllReservations()));
    }

    @FXML
    public void handleAddCar() {
        try {
            Vehicle v = new Vehicle();
            v.setName(modelField.getText());
            v.setCategory(categoryField.getText());
            v.setPriceRental(Double.parseDouble(priceField.getText()));
            // Defaults
            v.setPricePurchase(0);
            v.setDealershipID(1);
            v.setManufactureID(1);
            v.setStatus("AVAILABLE");

            vehicleDAO.addVehicle(v);
            refreshData();
            clearFields();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Car Added!");
            a.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteCar() {
        Vehicle selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            vehicleDAO.deleteVehicle(selected.getVehicleID());
            refreshData();
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Select a car to delete.");
            a.show();
        }
    }

    private void clearFields() {
        modelField.clear(); categoryField.clear(); priceField.clear();
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}