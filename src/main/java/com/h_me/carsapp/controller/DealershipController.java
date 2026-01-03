package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.DealershipDAO;
import com.h_me.carsapp.model.Dealerships;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class DealershipController {

    @FXML private TableView<Dealerships> dealerTable;
    @FXML private TableColumn<Dealerships, String> colName;
    @FXML private TableColumn<Dealerships, String> colCity;
    @FXML private TableColumn<Dealerships, Double> colLat;
    @FXML private TableColumn<Dealerships, Double> colLon;

    // CHANGED: New Fields
    @FXML private TextField cityField;
    @FXML private TextField nameField;

    @FXML private WebView mapWebView;

    private WebEngine webEngine;
    private boolean mapReady = false;
    private DealershipDAO dealershipDAO;

    @FXML
    public void initialize() {
        dealershipDAO = new DealershipDAO();

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colLat.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        colLon.setCellValueFactory(new PropertyValueFactory<>("longitude"));

        initMap();
        loadAllData();

        dealerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                focusOnDealership(newVal);
            }
        });
    }

    private void focusOnDealership(Dealerships d) {
        if (mapReady) {
            webEngine.executeScript("panToLocation(" + d.getLatitude() + ", " + d.getLongitude() + ")");
        }
    }

    // ... (Keep initMap logic exactly as it is) ...
    private void initMap() {
        webEngine = mapWebView.getEngine();
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' crossorigin=''/>\n" +
                "    <style>\n" +
                "        html, body { height: 100%; margin: 0; padding: 0; overflow: hidden; }\n" +
                "        #map { height: 100%; width: 100%; background: #f0f0f0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id='map'></div>\n" +
                "    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js' crossorigin=''></script>\n" +
                "    <script>\n" +
                "        L.Browser.any3d = false; // Keep this to prevent glitches\n" +
                "        var map;\n" +
                "        var markers = [];\n" +
                "        \n" +
                "        try {\n" +
                "            map = L.map('map', {\n" +
                "                zoomAnimation: false,\n" +
                "                fadeAnimation: false,\n" +
                "                markerZoomAnimation: false\n" +
                "            }).setView([33.5731, -7.5898], 6);\n" +
                "            \n" +
                "            L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {\n" +
                "                maxZoom: 19,\n" +
                "                attribution: '© CartoDB',\n" +
                "                noWrap: true\n" +
                "            }).addTo(map);\n" +
                "        } catch (e) { console.log('Init Error: ' + e); }\n" +
                "\n" +
                "        function clearMarkers() {\n" +
                "            if(!map) return;\n" +
                "            markers.forEach(m => map.removeLayer(m));\n" +
                "            markers = [];\n" +
                "        }\n" +
                "\n" +
                "        function addMarker(lat, lon, name) {\n" +
                "            if(!map) return;\n" +
                "            var m = L.marker([lat, lon]).addTo(map).bindPopup(name);\n" +
                "            markers.push(m);\n" +
                "        }\n" +
                "        \n" +
                "        // --- NEW FUNCTION: ZOOMS TO LOCATION ---\n" +
                "        function panToLocation(lat, lon) {\n" +
                "            if(!map) return;\n" +
                "            map.setView([lat, lon], 15); // Zoom level 15\n" +
                "        }\n" +
                "        \n" +
                "        function resizeMap() {\n" +
                "           if(map) { map.invalidateSize(); }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        webEngine.loadContent(htmlContent);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                mapReady = true;
                refreshMapMarkers(dealerTable.getItems());
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> webEngine.executeScript("resizeMap()"));
                pause.play();
            }
        });

        mapWebView.widthProperty().addListener((obs, oldVal, newVal) -> { if(mapReady) webEngine.executeScript("resizeMap()"); });
        mapWebView.heightProperty().addListener((obs, oldVal, newVal) -> { if(mapReady) webEngine.executeScript("resizeMap()"); });
    }

    @FXML
    public void loadAllData() {
        List<Dealerships> list = dealershipDAO.getAllDealerships();
        dealerTable.setItems(FXCollections.observableArrayList(list));
        refreshMapMarkers(list);
    }

    // CHANGED: Logic to search by text inputs
    @FXML
    public void handleSearch() {
        String city = cityField.getText() == null ? "" : cityField.getText().trim();
        String name = nameField.getText() == null ? "" : nameField.getText().trim();

        List<Dealerships> results = dealershipDAO.searchDealerships(city, name);
        dealerTable.setItems(FXCollections.observableArrayList(results));
        refreshMapMarkers(results);

        if (results.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("No dealerships found matching your criteria.");
            alert.show();
        }
    }

    private void refreshMapMarkers(List<Dealerships> dealerships) {
        if (!mapReady || dealerships == null) return;
        webEngine.executeScript("clearMarkers()");
        for (Dealerships d : dealerships) {
            String safeName = d.getName().replace("'", "\\'");
            webEngine.executeScript("addMarker(" + d.getLatitude() + ", " + d.getLongitude() + ", '" + safeName + "')");
        }
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