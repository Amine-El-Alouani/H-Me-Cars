package com.h_me.carsapp.controller;

import animatefx.animation.FadeIn;
import com.h_me.carsapp.dao.ReservationDAO;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.utils.StyledAlert;
import com.h_me.carsapp.utils.VehicleImageStore;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AdminController {

    // --- CAR TAB ---
    @FXML private TableView<Vehicle> carTable;
    @FXML private TableColumn<Vehicle, Integer> colCarId;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, String> colStatus;

    @FXML private TextField modelField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private Label imagePathLabel;
    
    // Stats labels
    @FXML private Label totalCarsLabel;
    @FXML private Label availableCarsLabel;
    @FXML private Label maintenanceCarsLabel;
    @FXML private Label rentedCarsLabel;

    // --- RESERVATION TAB ---
    @FXML private TableView<Reservation> resTable;
    @FXML private TableColumn<Reservation, Integer> colResId;
    @FXML private TableColumn<Reservation, Integer> colResCarId;
    @FXML private TableColumn<Reservation, Integer> colCost;
    @FXML private TableColumn<Reservation, String> colUserName;
    @FXML private TableColumn<Reservation, String> colUserPhone;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private ReservationDAO reservationDAO = new ReservationDAO();
    
    // Store selected image file
    private File selectedImageFile = null;

    @FXML
    public void initialize() {
        // Car Columns
        colCarId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Status column with color coding
        colStatus.setCellFactory(column -> new TableCell<Vehicle, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("AVAILABLE".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: 600;");
                    } else if ("MAINTENANCE".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: 600;");
                    } else if ("RENTED".equalsIgnoreCase(status)) {
                        setStyle("-fx-text-fill: #6366f1; -fx-font-weight: 600;");
                    } else {
                        setStyle("-fx-text-fill: #94a3b8;");
                    }
                }
            }
        });

        // Reservation Columns
        colResId.setCellValueFactory(new PropertyValueFactory<>("reservationID"));
        colResCarId.setCellValueFactory(new PropertyValueFactory<>("vehicleID"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Bind user detail columns
        colUserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colUserPhone.setCellValueFactory(new PropertyValueFactory<>("userPhone"));

        refreshData();
    }

    @FXML
    public void refreshData() {
        java.util.List<Vehicle> vehicles = vehicleDAO.getAllAvailableVehicles();
        carTable.setItems(FXCollections.observableArrayList(vehicles));
        resTable.setItems(FXCollections.observableArrayList(reservationDAO.getAllReservationsWithDetails()));
        
        // Update stats
        updateStats(vehicles);
        
        // Add subtle animation
        new FadeIn(carTable).setSpeed(2.0).play();
        new FadeIn(resTable).setSpeed(2.0).play();
    }
    
    private void updateStats(java.util.List<Vehicle> vehicles) {
        int total = vehicles.size();
        int available = 0;
        int maintenance = 0;
        int rented = 0;
        
        for (Vehicle v : vehicles) {
            String status = v.getStatus();
            if (status != null) {
                if ("AVAILABLE".equalsIgnoreCase(status)) available++;
                else if ("MAINTENANCE".equalsIgnoreCase(status)) maintenance++;
                else if ("RENTED".equalsIgnoreCase(status)) rented++;
            }
        }
        
        if (totalCarsLabel != null) totalCarsLabel.setText(String.valueOf(total));
        if (availableCarsLabel != null) availableCarsLabel.setText(String.valueOf(available));
        if (maintenanceCarsLabel != null) maintenanceCarsLabel.setText(String.valueOf(maintenance));
        if (rentedCarsLabel != null) rentedCarsLabel.setText(String.valueOf(rented));
    }

    @FXML
    public void handleToggleStatus() {
        Vehicle selected = carTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String current = selected.getStatus();
            String newStatus = current.equalsIgnoreCase("AVAILABLE") ? "MAINTENANCE" : "AVAILABLE";

            try {
                vehicleDAO.updateVehicleStatus(selected.getVehicleID(), newStatus);
                refreshData();
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
    public void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Car Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) modelField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            selectedImageFile = file;
            String fileName = file.getName();
            // Truncate long file names
            if (fileName.length() > 15) {
                fileName = fileName.substring(0, 12) + "...";
            }
            imagePathLabel.setText(fileName);
            imagePathLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #10b981;");
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
            v.setName(modelField.getText().trim());
            v.setCategory(categoryField.getText().trim());
            v.setPriceRental(Double.parseDouble(priceField.getText().trim()));
            v.setPricePurchase(0); 
            v.setDealershipID(0); // Set to 0 = NULL in database
            v.setManufactureID(0); // Set to 0 = NULL in database
            v.setStatus("AVAILABLE");
            
            // Handle image if selected
            if (selectedImageFile != null) {
                // Get images directory from VehicleImageStore
                Path imagesDir = VehicleImageStore.getImagesDir();
                Files.createDirectories(imagesDir);
                
                // Generate unique filename based on model name
                String cleanName = v.getName().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
                String extension = selectedImageFile.getName().substring(selectedImageFile.getName().lastIndexOf('.'));
                String newFileName = cleanName + "_" + System.currentTimeMillis() + extension;
                
                // Copy file to images directory
                Path targetPath = imagesDir.resolve(newFileName);
                Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Save to VehicleImageStore (by name, since we don't have ID yet)
                VehicleImageStore.saveImageForName(v.getName(), targetPath.toString());
            }
            
            vehicleDAO.addVehicle(v);
            refreshData();
            clearFields();
            StyledAlert.success("Car Added", "New car \"" + v.getName() + "\" has been added to inventory.");
        } catch (NumberFormatException e) {
            StyledAlert.error("Invalid Price", "Price must be a valid number.");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            StyledAlert.error("Database Error", "Could not add car: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            StyledAlert.warning("Image Warning", "Car was added but image could not be saved.");
            refreshData();
            clearFields();
        } catch (Exception e) { 
            e.printStackTrace(); 
            StyledAlert.error("Add Failed", "Could not add car to database: " + e.getMessage());
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

    private void clearFields() { 
        modelField.clear(); 
        categoryField.clear(); 
        priceField.clear();
        selectedImageFile = null;
        if (imagePathLabel != null) {
            imagePathLabel.setText("No image selected");
            imagePathLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/login-view.fxml"));
        Parent root = loader.load();
        
        // Fade animation for logout
        new FadeIn(root).setSpeed(1.5).play();
        
        Scene scene = stage.getScene();
        scene.setRoot(root);
        stage.setTitle("Login - H-Me Cars");
    }
}