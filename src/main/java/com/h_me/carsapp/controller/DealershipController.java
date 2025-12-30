package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.DealershipDAO;
import com.h_me.carsapp.model.Dealerships;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DealershipController {

    // --- FXML Elements (Must match your FXML file IDs) ---
    @FXML private WebView mapView;
    @FXML private TableView<Dealerships> dealerTable;
    @FXML private TableColumn<Dealerships, String> colName;
    @FXML private TableColumn<Dealerships, String> colCity;

    private WebEngine webEngine;
    private DealershipDAO dealershipDAO;
    private List<Dealerships> allDealers;

    @FXML
    public void initialize() {
        dealershipDAO = new DealershipDAO();
        webEngine = mapView.getEngine();


        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));

        allDealers = dealershipDAO.getAllDealerships();
        dealerTable.getItems().setAll(allDealers);

        if (allDealers.isEmpty()) {
            System.out.println("Warning: No dealerships found in database!");
        }

        URL url = getClass().getResource("/com/h_me/carsapp/view/map.html");
        if (url == null) {
            System.err.println("Error: map.html not found! Check resources/com/h_me/carsapp/view/");
            return;
        }
        webEngine.load(url.toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                loadMapPins();
            }
        });

        dealerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                moveToDealership(newSelection);
            }
        });
    }

    private void loadMapPins() {
        for (Dealerships d : allDealers) {
            if (d.getLatitude() != 0 && d.getLongitude() != 0) {
                String script = String.format("addPin(%f, %f, '%s', '%s');",
                        d.getLatitude(),
                        d.getLongitude(),
                        d.getName().replace("'", "\\'"),
                        d.getCity()
                );
                webEngine.executeScript(script);
            }
        }
    }

    private void moveToDealership(Dealerships d) {
        String script = String.format("flyToLocation(%f, %f);", d.getLatitude(), d.getLongitude());
        webEngine.executeScript(script);
    }

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}