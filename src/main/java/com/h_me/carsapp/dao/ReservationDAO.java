package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;
import java.time.LocalDateTime; // Important for dates
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public void createReservation(Reservation r) {
        String sql = "INSERT INTO reservations (typeres, startdate, enddate, totalcost, vehicleid, userid) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, r.getTypeRes());

            stmt.setTimestamp(2, Timestamp.valueOf(r.getStartDate()));

            if (r.getEndDate() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(r.getEndDate()));
            } else {
                stmt.setTimestamp(3, null);
            }

            stmt.setInt(4, r.getTotalCost());
            stmt.setInt(5, Integer.parseInt(String.valueOf(r.getVehicleID()))); // Assuming you fix model to int, remove ParseInt if fixed
            stmt.setInt(6, r.getUserID());

            stmt.executeUpdate();
            System.out.println("Reservation created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE userid = ?";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationID(rs.getInt("reservationid"));
                r.setTypeRes(rs.getString("typeres"));

                Timestamp startTs = rs.getTimestamp("startdate");
                if (startTs != null) r.setStartDate(startTs.toLocalDateTime());

                Timestamp endTs = rs.getTimestamp("enddate");
                if (endTs != null) r.setEndDate(endTs.toLocalDateTime());

                r.setTotalCost(rs.getInt("totalcost"));
                r.setVehicleID(Integer.parseInt(String.valueOf(rs.getInt("vehicleid")))); // Adapting to your String model
                r.setUserID(rs.getInt("userid"));

                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}