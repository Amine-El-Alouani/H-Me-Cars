package com.h_me.carsapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.SlideInRight;
import animatefx.animation.SlideInLeft;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.service.ReservationService;
import com.h_me.carsapp.utils.StyledAlert;
import com.h_me.carsapp.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

        // Custom status cell with color coding
        colStatus.setCellFactory(column -> new TableCell<Vehicle, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setStyle("");
                } else {
                    Vehicle v = getTableRow().getItem();
                    if (v.getAvailableFrom() != null && v.getAvailableFrom().isAfter(LocalDateTime.now())) {
                        setText("Unavailable until " + v.getAvailableFrom().toLocalDate());
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: 600;");
                    } else if ("AVAILABLE".equalsIgnoreCase(status)) {
                        setText(status);
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: 600;");
                    } else {
                        setText(status);
                        setStyle("-fx-text-fill: #94a3b8;");
                    }
                }
            }
        });

        loadData();
    }

    @FXML
    public void loadData() {
        List<Vehicle> data = vehicleDAO.getAllAvailableVehicles();
        vehicleList = FXCollections.observableArrayList(data);
        vehicleTable.setItems(vehicleList);
        
        // Add subtle animation when data loads
        new FadeIn(vehicleTable).setSpeed(2.0).play();
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
            StyledAlert.warning("No Selection", "Please select a car to rent.");
            return;
        }

        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            StyledAlert.warning("Missing Dates", "Please select Start and End dates.");
            return;
        }

        LocalDateTime start = startDatePicker.getValue().atStartOfDay();
        LocalDateTime end = endDatePicker.getValue().atTime(23, 59, 59);

        if (startDatePicker.getValue().isBefore(java.time.LocalDate.now())) {
            StyledAlert.error("Invalid Dates", "Start date cannot be in the past.");
            return;
        }

        if (end.isBefore(start)) {
            StyledAlert.error("Invalid Dates", "End date cannot be before start date.");
            return;
        }

        long days = ChronoUnit.DAYS.between(start, end);
        if (days < 1) days = 1;
        double estimatedCost = days * selectedCar.getPriceRental();

        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            StyledAlert.error("Not Logged In", "Please log out and log in again.");
            return;
        }

        try {
            int userId = Integer.parseInt(session.getUser().getUserID());
            boolean success = reservationService.processRental(selectedCar, userId, start, end);

            if (success) {
                String msg = String.format("Car: %s\nDays: %d\nTotal: %.2f MAD", selectedCar.getName(), days, estimatedCost);
                StyledAlert.success("Reservation Confirmed!", msg);
                loadData();
            } else {
                StyledAlert.error("Rental Failed", "Check the Console for Database Errors.");
            }

        } catch (NumberFormatException e) {
            StyledAlert.error("User ID Error", "Your User ID is not a number. Admin accounts cannot rent cars.");
        } catch (Exception e) {
            e.printStackTrace();
            StyledAlert.error("System Error", e.getMessage());
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        UserSession.cleanUserSession();
        
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Parent root = loader.load();
        
        // Fade animation for logout
        new FadeIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Login - H-Me Cars");
    }

    @FXML
    public void goToDealerships(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dealership-view.fxml"));
        Parent root = loader.load();
        
        // Slide right animation
        new SlideInRight(root).setSpeed(2.0).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Dealership Locator - H-Me Cars");
    }
}