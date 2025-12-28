package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // 1. GET ALL AVAILABLE CARS
    public List<Vehicle> getAllAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        // Use the EXACT column names from your screenshot
        String sql = "SELECT * FROM vehicles WHERE status = 'AVAILABLE'";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vehicle v = new Vehicle();
                // Map DB columns (lowercase) to Java fields
                v.setVehicleID(rs.getInt("vehicleid"));       // DB: vehicleid
                v.setName(rs.getString("model"));             // DB: model -> Java: name
                v.setCategory(rs.getString("category"));
                v.setPricePurchase(rs.getDouble("pricepurchase")); // DB: pricepurchase
                v.setPriceRental(rs.getDouble("pricerental"));     // DB: pricerental
                v.setStatus(rs.getString("status"));
                v.setDealershipID(rs.getInt("dealershipid"));      // DB: dealershipid
                v.setManufactureID(rs.getInt("manufacturerid"));   // DB: manufacturerid

                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. SEARCH BY LOCATION (Find cars at a specific dealership)
    public List<Vehicle> getVehiclesByDealership(int dealershipId) {
        List<Vehicle> list = new ArrayList<>();
        // Note: DB column is 'dealershipid'
        String sql = "SELECT * FROM vehicles WHERE dealershipid = ? AND status = 'AVAILABLE'";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dealershipId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleID(rs.getInt("vehicleid"));
                v.setName(rs.getString("model"));
                v.setCategory(rs.getString("category"));
                v.setPricePurchase(rs.getDouble("pricepurchase"));
                v.setPriceRental(rs.getDouble("pricerental"));
                v.setStatus(rs.getString("status"));
                v.setDealershipID(rs.getInt("dealershipid"));
                v.setManufactureID(rs.getInt("manufacturerid"));
                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. ADD VEHICLE
    public void addVehicle(Vehicle v) {
        // Corrected column names for INSERT
        String sql = "INSERT INTO vehicles (model, category, pricepurchase, pricerental, status, dealershipid, manufacturerid) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getName()); // Java 'Name' goes into DB 'model'
            stmt.setString(2, v.getCategory());
            stmt.setDouble(3, v.getPricePurchase());
            stmt.setDouble(4, v.getPriceRental());
            stmt.setString(5, "AVAILABLE");
            stmt.setInt(6, v.getDealershipID());
            stmt.setInt(7, v.getManufactureID());

            stmt.executeUpdate();
            System.out.println("✅ Vehicle added to database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}