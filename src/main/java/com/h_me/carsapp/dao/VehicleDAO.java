package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {


    public List<Vehicle> getAllAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        // Fetch all vehicles, and find the latest future end date for reservations using Java's time
        String sql = "SELECT v.*, " +
                     "(SELECT MAX(r.enddate) FROM reservations r WHERE r.vehicleid = v.vehicleid AND r.enddate >= ?) as reserved_until " +
                     "FROM vehicles v";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ResultSet rs = stmt.executeQuery();

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
                
                Timestamp reservedUntil = rs.getTimestamp("reserved_until");
                if (reservedUntil != null) {
                    v.setAvailableFrom(reservedUntil.toLocalDateTime());
                }

                list.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Vehicle> getVehiclesByDealership(int dealershipId) {
        List<Vehicle> list = new ArrayList<>();
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

    public void addVehicle(Vehicle v) throws SQLException {
        // First, get the next available vehicleid
        String maxIdSql = "SELECT COALESCE(MAX(vehicleid), 0) + 1 FROM vehicles";
        int newVehicleId;
        
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement maxStmt = conn.prepareStatement(maxIdSql);
             ResultSet rs = maxStmt.executeQuery()) {
            rs.next();
            newVehicleId = rs.getInt(1);
        }
        
        String sql = "INSERT INTO vehicles (vehicleid, model, category, pricepurchase, pricerental, status, dealershipid, manufacturerid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newVehicleId);
            stmt.setString(2, v.getName());
            stmt.setString(3, v.getCategory());
            stmt.setDouble(4, v.getPricePurchase());
            stmt.setDouble(5, v.getPriceRental());
            stmt.setString(6, "AVAILABLE");
            
            // Set dealership/manufacturer to NULL if they're 0
            if (v.getDealershipID() > 0) {
                stmt.setInt(7, v.getDealershipID());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            if (v.getManufactureID() > 0) {
                stmt.setInt(8, v.getManufactureID());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vehicle added to database with ID: " + newVehicleId);
            } else {
                throw new SQLException("No rows inserted - INSERT failed silently");
            }
        }
    }



    public void updateVehicleStatus(int vehicleId, String newStatus) {
        String sql = "UPDATE vehicles SET status = ? WHERE vehicleid = ?";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, vehicleId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicleid = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            stmt.executeUpdate();
            System.out.println("Vehicle deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAllVehicles() throws SQLException {
        String sql = "DELETE FROM vehicles";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int deleted = stmt.executeUpdate();
            System.out.println("Deleted " + deleted + " vehicles.");
        }
    }
}