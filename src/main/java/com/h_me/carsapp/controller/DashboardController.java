package com.h_me.carsapp.controller;

import animatefx.animation.FadeIn;
import animatefx.animation.SlideInRight;
import com.h_me.carsapp.dao.VehicleDAO;
import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.service.ReservationService;
import com.h_me.carsapp.utils.StyledAlert;
import com.h_me.carsapp.utils.UserSession;
import com.h_me.carsapp.utils.VehicleImageStore;
import com.h_me.carsapp.utils.CarBrandDetector;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardController {

    @FXML private FlowPane vehicleCardsPane;
    @FXML private ScrollPane vehicleScrollPane;
    @FXML private TextField searchField;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    private VehicleDAO vehicleDAO;
    private ReservationService reservationService;
    private List<Vehicle> vehicleList;
    private Vehicle selectedVehicle;
    private VBox selectedCard;
    
    // Cache for car images by category
    private Map<String, Image> categoryImages = new HashMap<>();
    
    // Thread pool for lazy image loading (limited to avoid overwhelming the DB)
    private static final ExecutorService imageLoaderPool = Executors.newFixedThreadPool(3);
    
    // Cache for loaded vehicle images (shared across all cards)
    private static final Map<Integer, Image> vehicleImageCache = new HashMap<>();

    @FXML
    public void initialize() {
        vehicleDAO = new VehicleDAO();
        reservationService = new ReservationService();
        
        // Load category images
        loadCategoryImages();
        
        // Load data asynchronously to avoid blocking UI
        loadData();
    }
    
    private void loadCategoryImages() {
        String[] categories = {"sedan", "suv", "sports", "luxury", "economy"};
        for (String category : categories) {
            try {
                InputStream is = getClass().getResourceAsStream("/com/h_me/carsapp/images/cars/" + category + ".png");
                if (is != null) {
                    categoryImages.put(category.toLowerCase(), new Image(is, 200, 110, false, true));
                }
            } catch (Exception e) {
                System.out.println("Could not load image for category: " + category);
            }
        }
    }

    @FXML
    public void loadData() {
        // Show loading indicator
        vehicleCardsPane.getChildren().clear();
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);
        VBox loadingBox = new VBox(16, loadingIndicator, new Label("Loading vehicles..."));
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.getStyleClass().add("loading-container");
        vehicleCardsPane.getChildren().add(loadingBox);
        
        // Load data in background thread
        Task<List<Vehicle>> loadTask = new Task<>() {
            @Override
            protected List<Vehicle> call() {
                return vehicleDAO.getAllAvailableVehicles();
            }
        };
        
        loadTask.setOnSucceeded(event -> {
            vehicleList = loadTask.getValue();
            Platform.runLater(() -> {
                displayVehicleCards(vehicleList);
                new FadeIn(vehicleCardsPane).setSpeed(2.0).play();
            });
        });
        
        loadTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                vehicleCardsPane.getChildren().clear();
                Label errorLabel = new Label("Failed to load vehicles. Click Refresh to try again.");
                errorLabel.getStyleClass().add("error-label");
                vehicleCardsPane.getChildren().add(errorLabel);
            });
        });
        
        new Thread(loadTask).start();
    }
    
    private void displayVehicleCards(List<Vehicle> vehicles) {
        vehicleCardsPane.getChildren().clear();
        selectedVehicle = null;
        selectedCard = null;
        
        if (vehicles.isEmpty()) {
            Label emptyLabel = new Label("No vehicles available at this time.");
            emptyLabel.getStyleClass().add("label-muted");
            vehicleCardsPane.getChildren().add(emptyLabel);
            return;
        }
        
        for (Vehicle vehicle : vehicles) {
            VBox card = createVehicleCard(vehicle);
            vehicleCardsPane.getChildren().add(card);
        }
    }
    
    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox();
        card.getStyleClass().add("vehicle-card");
        
        // Image container
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("vehicle-card-image-container");
        imageContainer.setMinSize(260, 150);
        imageContainer.setMaxSize(260, 150);
        imageContainer.setPrefSize(260, 150);
        
        // Clip for rounded corners
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(260, 150);
        clip.setArcWidth(30);
        clip.setArcHeight(30);
        imageContainer.setClip(clip);
        
        // Create placeholder first
        Label placeholderIcon = new Label("\uD83D\uDE97");
        placeholderIcon.setStyle("-fx-font-size: 48px; -fx-text-fill: #64748b;");
        
        // Check if we already have the image cached
        if (vehicleImageCache.containsKey(vehicle.getVehicleID())) {
            Image cachedImage = vehicleImageCache.get(vehicle.getVehicleID());
            ImageView imageView = createImageView(cachedImage);
            imageContainer.getChildren().add(imageView);
        } else if (vehicle.getImageData() != null && vehicle.getImageData().length > 0) {
            // Image data already loaded (from DAO cache)
            try {
                Image img = new Image(new java.io.ByteArrayInputStream(vehicle.getImageData()));
                vehicleImageCache.put(vehicle.getVehicleID(), img);
                ImageView imageView = createImageView(img);
                imageContainer.getChildren().add(imageView);
            } catch (Exception e) {
                imageContainer.getChildren().add(placeholderIcon);
                lazyLoadImage(vehicle.getVehicleID(), imageContainer, placeholderIcon);
            }
        } else {
            // Show placeholder and lazy load the image
            imageContainer.getChildren().add(placeholderIcon);
            lazyLoadImage(vehicle.getVehicleID(), imageContainer, placeholderIcon);
        }
        
        // Content container
        VBox content = new VBox(8);
        content.getStyleClass().add("vehicle-card-content");
        
        // Vehicle name with brand badge
        HBox nameBox = new HBox(6);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        
        // Try to create a brand badge (logo or text fallback)
        javafx.scene.Node brandBadge = CarBrandDetector.createBrandBadge(vehicle.getName());
        if (brandBadge != null) {
            nameBox.getChildren().add(brandBadge);
        }
        
        Label nameLabel = new Label(vehicle.getName());
        nameLabel.getStyleClass().add("vehicle-card-name");
        nameBox.getChildren().add(nameLabel);
        
        // Category badge
        Label categoryLabel = new Label(vehicle.getCategory() != null ? vehicle.getCategory().toUpperCase() : "N/A");
        categoryLabel.getStyleClass().add("vehicle-card-category");
        
        // Price
        HBox priceBox = new HBox(4);
        priceBox.setAlignment(Pos.BASELINE_LEFT);
        Label priceLabel = new Label(String.format("%.0f MAD", vehicle.getPriceRental()));
        priceLabel.getStyleClass().add("vehicle-card-price");
        Label perDayLabel = new Label("/ day");
        perDayLabel.getStyleClass().add("vehicle-card-price-label");
        priceBox.getChildren().addAll(priceLabel, perDayLabel);
        
        // Status
        Label statusLabel = new Label();
        if (vehicle.getAvailableFrom() != null && vehicle.getAvailableFrom().isAfter(LocalDateTime.now())) {
            statusLabel.setText("Available from " + vehicle.getAvailableFrom().toLocalDate());
            statusLabel.getStyleClass().add("vehicle-card-status-unavailable");
        } else if ("AVAILABLE".equalsIgnoreCase(vehicle.getStatus())) {
            statusLabel.setText("✓ Available Now");
            statusLabel.getStyleClass().add("vehicle-card-status-available");
        } else {
            statusLabel.setText(vehicle.getStatus());
            statusLabel.getStyleClass().add("vehicle-card-status-unavailable");
        }
        
        content.getChildren().addAll(nameBox, categoryLabel, priceBox, statusLabel);
        card.getChildren().addAll(imageContainer, content);
        
        // Click handler for selection
        card.setOnMouseClicked(e -> selectVehicle(vehicle, card));
        
        return card;
    }
    
    /**
     * Create a properly configured ImageView for a car image
     */
    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        StackPane.setAlignment(imageView, Pos.CENTER);
        return imageView;
    }
    
    /**
     * Lazy load image from database in background thread
     */
    private void lazyLoadImage(int vehicleId, StackPane imageContainer, Label placeholder) {
        imageLoaderPool.submit(() -> {
            try {
                byte[] imageData = vehicleDAO.getVehicleImage(vehicleId);
                if (imageData != null && imageData.length > 0) {
                    Image img = new Image(new java.io.ByteArrayInputStream(imageData));
                    vehicleImageCache.put(vehicleId, img);
                    
                    Platform.runLater(() -> {
                        imageContainer.getChildren().clear();
                        ImageView imageView = createImageView(img);
                        imageContainer.getChildren().add(imageView);
                        // Fade in the new image
                        new FadeIn(imageView).setSpeed(2.0).play();
                    });
                }
                // If no image data, keep the placeholder
            } catch (Exception e) {
                System.err.println("Failed to lazy load image for vehicle " + vehicleId + ": " + e.getMessage());
            }
        });
    }
    
    private Image getImageForVehicle(Vehicle vehicle) {
        // Check for image data from database
        if (vehicle.getImageData() != null && vehicle.getImageData().length > 0) {
            try {
                java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(vehicle.getImageData());
                // Load without size constraints to get full resolution
                Image img = new Image(bis);
                System.out.println("Loaded DB image: " + img.getWidth() + "x" + img.getHeight());
                return img;
            } catch (Exception e) {
                System.out.println("Could not load database image: " + e.getMessage());
            }
        }
        
        // Try local file path
        if (vehicle.getImagePath() != null && !vehicle.getImagePath().isEmpty()) {
            try {
                java.io.File imageFile = new java.io.File(vehicle.getImagePath());
                if (imageFile.exists()) {
                    return new Image(imageFile.toURI().toString());
                }
            } catch (Exception e) {
                System.out.println("Could not load local image: " + vehicle.getImagePath());
            }
        }
        
        return null;
    }
    
    private Image getImageForCategory(String category) {
        if (category == null) return categoryImages.get("sedan");
        
        String lowerCategory = category.toLowerCase();
        
        // Map common category names to our image categories
        if (lowerCategory.contains("suv") || lowerCategory.contains("crossover") || lowerCategory.contains("4x4")) {
            return categoryImages.get("suv");
        } else if (lowerCategory.contains("sport") || lowerCategory.contains("coupe") || lowerCategory.contains("convertible")) {
            return categoryImages.get("sports");
        } else if (lowerCategory.contains("luxury") || lowerCategory.contains("premium") || lowerCategory.contains("executive")) {
            return categoryImages.get("luxury");
        } else if (lowerCategory.contains("economy") || lowerCategory.contains("compact") || lowerCategory.contains("hatchback")) {
            return categoryImages.get("economy");
        } else if (lowerCategory.contains("sedan") || lowerCategory.contains("saloon")) {
            return categoryImages.get("sedan");
        }
        
        // Default to sedan
        return categoryImages.get("sedan");
    }
    
    private void selectVehicle(Vehicle vehicle, VBox card) {
        // Deselect previous card
        if (selectedCard != null) {
            selectedCard.getStyleClass().remove("vehicle-card-selected");
        }
        
        // Select new card
        selectedVehicle = vehicle;
        selectedCard = card;
        card.getStyleClass().add("vehicle-card-selected");
    }

    @FXML
    public void handleSearch() {
        if (vehicleList == null) return;
        
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            displayVehicleCards(vehicleList);
            return;
        }
        
        List<Vehicle> filteredList = vehicleList.stream()
                .filter(v -> v.getName().toLowerCase().contains(query) ||
                        v.getCategory().toLowerCase().contains(query))
                .toList();
        
        displayVehicleCards(filteredList);
    }

    @FXML
    public void handleRent() {
        if (selectedVehicle == null) {
            StyledAlert.warning("No Selection", "Please click on a car card to select it for rental.");
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
        double estimatedCost = days * selectedVehicle.getPriceRental();

        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            StyledAlert.error("Not Logged In", "Please log out and log in again.");
            return;
        }

        try {
            int userId = Integer.parseInt(session.getUser().getUserID());
            
            // Process rental in background
            final long finalDays = days;
            Task<Boolean> rentTask = new Task<>() {
                @Override
                protected Boolean call() {
                    return reservationService.processRental(selectedVehicle, userId, start, end);
                }
            };
            
            rentTask.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    if (rentTask.getValue()) {
                        String msg = String.format("Car: %s\nDays: %d\nTotal: %.2f MAD", 
                                selectedVehicle.getName(), finalDays, estimatedCost);
                        StyledAlert.success("Reservation Confirmed!", msg);
                        loadData();
                    } else {
                        StyledAlert.error("Rental Failed", "Check the Console for Database Errors.");
                    }
                });
            });
            
            rentTask.setOnFailed(event -> {
                Platform.runLater(() -> {
                    StyledAlert.error("System Error", "An error occurred while processing your rental.");
                });
            });
            
            new Thread(rentTask).start();

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