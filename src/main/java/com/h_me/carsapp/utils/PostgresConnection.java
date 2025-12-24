package com.h_me.carsapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    // 1. Database Credentials
    // For local development, these are usually "postgres" and your password
    private static final String URL = "jdbc:postgresql://localhost:5432/hme_cars";
    private static final String USER = "postgres";
    private static final String PASSWORD = "YOUR_PASSWORD_HERE"; // <--- CHANGE THIS

    // 2. The single instance of the connection
    private static Connection connection = null;

    // Private constructor to prevent "new PostgresConnection()"
    private PostgresConnection() {}

    /**
     * Gets the active database connection.
     * If one doesn't exist, it creates it.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load the PostgreSQL Driver
                Class.forName("org.postgresql.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Successfully connected to PostgreSQL!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL JDBC Driver not found. Add the dependency!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Connection failed. Check URL, User, or Password.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Utility method to close the connection when the app exits.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}