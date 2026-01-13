package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Vehicle;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleDAO {
    
    // Cache for loaded images to avoid redundant database calls
    private static final Map<Integer, byte[]> imageCache = new ConcurrentHashMap<>();

    /**
     * Get all available vehicles WITHOUT loading images (for fast initial load).
     * Images should be loaded lazily via getVehicleImage().
     */
    public List<Vehicle> getAllAvailableVehicles() {
        List<Vehicle> list = new ArrayList<>();
        // Note: We exclude the 'image' column for faster loading
        String sql = "SELECT v.vehicleid, v.model, v.category, v.pricepurchase, v.pricerental, v.status, v.dealershipid, v.manufacturerid, " +
                     "(SELECT MAX(r.enddate) FROM reservations r WHERE r.vehicleid = v.vehicleid AND r.enddate >= ?) as reserved_until " +
                     "FROM vehicles v";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
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
                
                // Check if we have a cached image
                if (imageCache.containsKey(v.getVehicleID())) {
                    v.setImageData(imageCache.get(v.getVehicleID()));
                }
                // Note: We don't load image data here anymore for faster initial load
                
                Timestamp reservedUntil = rs.getTimestamp("reserved_until");
                if (reservedUntil != null) {
                    v.setAvailableFrom(reservedUntil.toLocalDateTime());
                }

                list.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vehicles: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Load image for a specific vehicle (for lazy loading).
     * Results are cached to avoid redundant database calls.
     * 
     * @param vehicleId The vehicle ID to load image for
     * @return The image bytes, or null if no image exists
     */
    public byte[] getVehicleImage(int vehicleId) {
        // Check cache first
        if (imageCache.containsKey(vehicleId)) {
            return imageCache.get(vehicleId);
        }
        
        String sql = "SELECT image FROM vehicles WHERE vehicleid = ?";
        
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                byte[] imageData = rs.getBytes("image");
                if (imageData != null && imageData.length > 0) {
                    // Cache the result
                    imageCache.put(vehicleId, imageData);
                    return imageData;
                }
            }
        } catch (SQLException e) {
            // Image column might not exist or other error
            System.err.println("Could not load image for vehicle " + vehicleId + ": " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Clear the image cache (useful when images are updated)
     */
    public void clearImageCache() {
        imageCache.clear();
    }
    
    /**
     * Clear a specific vehicle's cached image
     */
    public void clearImageCache(int vehicleId) {
        imageCache.remove(vehicleId);
    }

    public List<Vehicle> getVehiclesByDealership(int dealershipId) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT vehicleid, model, category, pricepurchase, pricerental, status, dealershipid, manufacturerid " +
                     "FROM vehicles WHERE dealershipid = ? AND status = 'AVAILABLE'";

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
                
                // Check cache for image
                if (imageCache.containsKey(v.getVehicleID())) {
                    v.setImageData(imageCache.get(v.getVehicleID()));
                }
                
                list.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vehicles by dealership: " + e.getMessage());
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
        
        // Check if image column exists, if so include it
        boolean hasImageColumn = checkImageColumnExists();
        boolean hasImage = v.getImageData() != null && v.getImageData().length > 0;
        
        System.out.println("DEBUG: Image column exists: " + hasImageColumn);
        System.out.println("DEBUG: Vehicle has image data: " + hasImage + (hasImage ? " (" + v.getImageData().length + " bytes)" : ""));
        
        String sql;
        if (hasImageColumn && hasImage) {
            sql = "INSERT INTO vehicles (vehicleid, model, category, pricepurchase, pricerental, status, dealershipid, manufacturerid, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO vehicles (vehicleid, model, category, pricepurchase, pricerental, status, dealershipid, manufacturerid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }
        
        System.out.println("DEBUG: Using SQL: " + (hasImageColumn && hasImage ? "with image" : "without image"));

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
            
            // Set image data if column exists and we have data
            if (hasImageColumn && hasImage) {
                stmt.setBytes(9, v.getImageData());
                System.out.println("DEBUG: Setting image bytes in prepared statement");
                // Cache the new image
                imageCache.put(newVehicleId, v.getImageData());
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vehicle added to database with ID: " + newVehicleId);
            } else {
                throw new SQLException("No rows inserted - INSERT failed silently");
            }
        }
    }
    
    /**
     * Check if the image column exists in the vehicles table
     */
    private boolean checkImageColumnExists() {
        String sql = "SELECT column_name FROM information_schema.columns WHERE table_name = 'vehicles' AND column_name = 'image'";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Create the image column if it doesn't exist
     */
    public void ensureImageColumn() {
        if (!checkImageColumnExists()) {
            String sql = "ALTER TABLE vehicles ADD COLUMN image BYTEA";
            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
                System.out.println("Added 'image' column to vehicles table");
            } catch (SQLException e) {
                System.out.println("Could not add image column: " + e.getMessage());
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
            System.err.println("Error updating vehicle status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicleid = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            stmt.executeUpdate();
            // Clear from cache
            imageCache.remove(vehicleId);
            System.out.println("Vehicle deleted.");
        } catch (SQLException e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void deleteAllVehicles() throws SQLException {
        String sql = "DELETE FROM vehicles";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int deleted = stmt.executeUpdate();
            // Clear entire cache
            imageCache.clear();
            System.out.println("Deleted " + deleted + " vehicles.");
        }
    }
}