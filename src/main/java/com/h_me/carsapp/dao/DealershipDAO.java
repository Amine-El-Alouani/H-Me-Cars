package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Dealerships;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DealershipDAO {

    public List<Dealerships> getAllDealerships() {
        List<Dealerships> list = new ArrayList<>();
        String sql = "SELECT * FROM dealerships";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Dealerships d = new Dealerships();
                d.setDealershipID(rs.getInt("dealershipid")); // DB: dealershipid
                d.setName(rs.getString("name"));
                d.setCity(rs.getString("city"));
                d.setLatitude(rs.getDouble("latitude"));
                d.setLongitude(rs.getDouble("longitude"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching dealerships: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Dealerships> findNearby(double userLat, double userLon, double radiusKm) {
        List<Dealerships> list = new ArrayList<>();

        String sql = "SELECT *, (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) AS distance " +
                "FROM dealerships " +
                "WHERE (6371 * acos(cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) + sin(radians(?)) * sin(radians(latitude)))) < ? " +
                "ORDER BY distance ASC";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, userLat);
            stmt.setDouble(2, userLon);
            stmt.setDouble(3, userLat);

            stmt.setDouble(4, userLat);
            stmt.setDouble(5, userLon);
            stmt.setDouble(6, userLat);

            stmt.setDouble(7, radiusKm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Dealerships d = new Dealerships();
                d.setDealershipID(rs.getInt("dealershipid"));
                d.setName(rs.getString("name"));
                d.setCity(rs.getString("city"));
                d.setLatitude(rs.getDouble("latitude"));
                d.setLongitude(rs.getDouble("longitude"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error finding nearby dealerships: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Dealerships> searchDealerships(String city, String name) {
        List<Dealerships> list = new ArrayList<>();

        // precise search not required (uses partial match with %)
        String sql = "SELECT * FROM dealerships WHERE LOWER(city) LIKE ? AND LOWER(name) LIKE ?";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + city.toLowerCase() + "%");
            stmt.setString(2, "%" + name.toLowerCase() + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Dealerships d = new Dealerships();
                d.setDealershipID(rs.getInt("dealershipid"));
                d.setName(rs.getString("name"));
                d.setCity(rs.getString("city"));
                d.setLatitude(rs.getDouble("latitude"));
                d.setLongitude(rs.getDouble("longitude"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Error searching dealerships: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}