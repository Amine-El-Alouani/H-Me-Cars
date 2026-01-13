package com.h_me.carsapp.dao;

import com.h_me.carsapp.model.User;
import com.h_me.carsapp.utils.PostgresConnection;

import java.sql.*;

public class UserDAO {

    public User validateLogin(String email, String password) {
        String sql = "SELECT * FROM app_users WHERE email = ? AND password = ?";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getString("userid")); // DB is Varchar
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNum(rs.getInt("phonenum"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Login validation error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO app_users (userid, firstname, lastname, phonenum, email, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUserID());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setInt(4, user.getPhoneNum());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPassword());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}