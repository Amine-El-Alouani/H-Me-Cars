package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean isCarAvailable(int vehicleId, LocalDateTime newStart, LocalDateTime newEnd) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE vehicleid = ? AND (startdate < ? AND enddate > ?)";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            stmt.setTimestamp(2, Timestamp.valueOf(newEnd));
            stmt.setTimestamp(3, Timestamp.valueOf(newStart));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reservation> getAllReservationsWithDetails() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.firstname, u.lastname, u.phonenum " +
                "FROM reservations r " +
                "JOIN app_users u ON CAST(r.userid AS VARCHAR) = u.userid";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationID(rs.getInt("reservationid"));
                r.setTypeRes(rs.getString("typeres"));

                if (rs.getTimestamp("startdate") != null)
                    r.setStartDate(rs.getTimestamp("startdate").toLocalDateTime());
                if (rs.getTimestamp("enddate") != null)
                    r.setEndDate(rs.getTimestamp("enddate").toLocalDateTime());

                r.setTotalCost(rs.getInt("totalcost"));
                r.setVehicleID(rs.getInt("vehicleid"));
                r.setUserID(rs.getInt("userid"));

                String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                r.setUserName(fullName);
                r.setUserPhone(String.valueOf(rs.getInt("phonenum")));

                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public void createReservation(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservations (typeres, startdate, enddate, totalcost, vehicleid, userid) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, r.getTypeRes());
            stmt.setTimestamp(2, Timestamp.valueOf(r.getStartDate()));
            stmt.setTimestamp(3, r.getEndDate() != null ? Timestamp.valueOf(r.getEndDate()) : null);
            stmt.setInt(4, r.getTotalCost());
            stmt.setInt(5, r.getVehicleID());
            stmt.setInt(6, r.getUserID());

            stmt.executeUpdate();
            System.out.println("Reservation created successfully!");
        }
    }

    public List<Reservation> getAllReservations() {
        return getAllReservationsWithDetails();
    }
}