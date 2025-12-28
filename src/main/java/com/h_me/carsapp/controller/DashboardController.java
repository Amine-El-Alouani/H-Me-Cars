package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DashboardController {

    // 1. Link to FXML Elements
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> colName;
    @FXML private TableColumn<Vehicle, String> colCategory;
    @FXML private TableColumn<Vehicle, Double> colPrice;
    @FXML private TableColumn<Vehicle, String> colStatus;
    @FXML private TextField searchField;

    // 2. Data Tools
    private VehicleDAO vehicleDAO;
    private ObservableList<Vehicle> vehicleList; // Special list for JavaFX

    // 3. Initialize (Runs automatically when screen opens)
    @FXML
    public void initialize() {
        vehicleDAO = new VehicleDAO();

        // Setup Columns: Tell JavaFX which field in 'Vehicle' goes to which column
        // These strings MUST match your Vehicle.java field names exactly!
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("priceRental"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load data immediately
        loadData();
    }

    // 4. Load Data from Postgres
    @FXML
    public void loadData() {
        List<Vehicle> data = vehicleDAO.getAllAvailableVehicles();
        vehicleList = FXCollections.observableArrayList(data);
        vehicleTable.setItems(vehicleList);
    }

    // 5. Search Button Logic
    @FXML
    public void handleSearch() {
        String query = searchField.getText().toLowerCase();

        // Filter the existing list locally (easier than SQL for small data)
        ObservableList<Vehicle> filteredList = vehicleList.filtered(v ->
                v.getName().toLowerCase().contains(query) ||
                        v.getCategory().toLowerCase().contains(query)
        );

        vehicleTable.setItems(filteredList);
    }
}