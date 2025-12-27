package com.h_me.carsapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    // 1. Database Credentials
    // Replace these strings with your actual Neon details if not using Environment Variables
    private static final String URL = "jdbc:postgresql://ep-rough-hat-ag8k4p8x-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";

    // Ideally use System.getenv("DB_PASSWORD"), but for testing you can paste it here:
    private static final String PASSWORD = "npg_YGyx47CvOmsf";

    // 2. The single instance of the connection (Singleton Pattern)
    private static Connection connection = null;

    // Private constructor prevents creating new instances like "new PostgresConnection()"
    private PostgresConnection() {}

    /**
     * Gets the active database connection.
     * Checks if a connection exists; if not, creates one.
     */
    public static Connection getConnection() {
        try {
            // Check if connection is null or closed, then reconnect
            if (connection == null || connection.isClosed()) {

                // Load the PostgreSQL Driver (optional in newer Java, but good practice)
                try {
                    Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("❌ PostgreSQL JDBC Driver not found. Add the dependency to pom.xml.");
                    return null;
                }

                // Attempt authentication
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Successfully connected to PostgreSQL!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Connection failed!");
            System.err.println("Error: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Closes the connection. Call this when your app shuts down.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // =============================================================
    // TEST AREA: Run this file directly to verify your connection
    // =============================================================
    public static void main(String[] args) {
        System.out.println("Testing connection to Neon DB...");

        Connection testConn = PostgresConnection.getConnection();

        if (testConn != null) {
            System.out.println("Test passed! You can now use this class in your main app.");
            // Optional: Close it after test
            PostgresConnection.closeConnection();
        } else {
            System.out.println("Test failed. Please check your password.");
        }
    }
}