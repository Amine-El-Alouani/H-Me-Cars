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

    @FXML private TextField latField;
    @FXML private TextField lonField;

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
    }

    private void initMap() {
        webEngine = mapWebView.getEngine();

        // 1. Force a browser-like User-Agent to avoid some blocks
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css' crossorigin=''/>\n" +
                "    <style>\n" +
                "        html, body { height: 100%; margin: 0; padding: 0; }\n" +
                "        #map { height: 100%; width: 100%; background: #f0f0f0; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id='map'></div>\n" +
                "    <script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js' crossorigin=''></script>\n" +
                "    <script>\n" +
                "        var map;\n" +
                "        var markers = [];\n" +
                "        \n" +
                "        try {\n" +
                "            map = L.map('map').setView([33.5731, -7.5898], 6);\n" +
                "            \n" +
                "            // FIX: CHANGED TO CartoDB (They allow JavaFX apps!)\n" +
                "            L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {\n" +
                "                maxZoom: 19,\n" +
                "                attribution: '© CartoDB'\n" +
                "            }).addTo(map);\n" +
                "            \n" +
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
                "        function resizeMap() {\n" +
                "           if(map) { \n" +
                "               map.invalidateSize(); \n" +
                "           }\n" +
                "        }\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        webEngine.loadContent(htmlContent);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                mapReady = true;
                refreshMapMarkers(dealerTable.getItems());

                // Keep the resize fix for the grey tiles
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> webEngine.executeScript("resizeMap()"));
                pause.play();
            }
        });

        // Resize listeners
        mapWebView.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(mapReady) webEngine.executeScript("resizeMap()");
        });
        mapWebView.heightProperty().addListener((obs, oldVal, newVal) -> {
            if(mapReady) webEngine.executeScript("resizeMap()");
        });
    }

    @FXML
    public void loadAllData() {
        List<Dealerships> list = dealershipDAO.getAllDealerships();
        dealerTable.setItems(FXCollections.observableArrayList(list));
        refreshMapMarkers(list);
    }

    @FXML
    public void handleSearchNearby() {
        try {
            double lat = Double.parseDouble(latField.getText());
            double lon = Double.parseDouble(lonField.getText());

            List<Dealerships> nearby = dealershipDAO.findNearby(lat, lon, 50.0);
            dealerTable.setItems(FXCollections.observableArrayList(nearby));
            refreshMapMarkers(nearby);

            if(nearby.isEmpty()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("No dealerships found within 50km.");
                alert.show();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please enter valid numbers for Lat/Lon.");
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