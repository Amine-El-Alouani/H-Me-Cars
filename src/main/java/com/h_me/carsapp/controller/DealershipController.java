package com.h_me.carsapp.controller;

import com.h_me.carsapp.dao.DealershipDAO;
import com.h_me.carsapp.model.Dealerships;
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

public class DealershipController {

    @FXML private TableView<Dealerships> dealerTable;
    @FXML private TableColumn<Dealerships, String> colName;
    @FXML private TableColumn<Dealerships, String> colCity;
    @FXML private TableColumn<Dealerships, Double> colLat;
    @FXML private TableColumn<Dealerships, Double> colLon;

    @FXML private TextField latField;
    @FXML private TextField lonField;

    private DealershipDAO dealershipDAO;

    @FXML
    public void initialize() {
        dealershipDAO = new DealershipDAO();

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colLat.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        colLon.setCellValueFactory(new PropertyValueFactory<>("longitude"));

        loadAllData();
    }

    @FXML
    public void loadAllData() {
        List<Dealerships> list = dealershipDAO.getAllDealerships();
        dealerTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    public void handleSearchNearby() {
        try {
            double lat = Double.parseDouble(latField.getText());
            double lon = Double.parseDouble(lonField.getText());

            List<Dealerships> nearby = dealershipDAO.findNearby(lat, lon, 50.0);
            dealerTable.setItems(FXCollections.observableArrayList(nearby));

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

    @FXML
    public void goBack(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/h_me/carsapp/view/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}