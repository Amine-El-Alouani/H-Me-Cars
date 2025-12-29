package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.Reservation;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationID(rs.getInt("reservationid"));
                r.setTypeRes(rs.getString("typeres"));
                if (rs.getTimestamp("startDate") != null) {
                    r.setStartDate(rs.getTimestamp("startDate").toLocalDateTime());
                }
                if (rs.getTimestamp("endDate") != null) {
                    r.setEndDate(rs.getTimestamp("endDate").toLocalDateTime());
                }
                r.setTotalCost(rs.getInt("totalCostd"));

                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
