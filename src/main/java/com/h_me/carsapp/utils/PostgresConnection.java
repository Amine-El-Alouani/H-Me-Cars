package com.h_me.carsapp.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnection {

    // 1. Database Credentials
    // For local development, these are usually "postgres" and your password
    // SECURITY: Read credentials from Environment Variables. Do not hardcode secrets.
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://ep-rough-hat-ag8k4p8x-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "neondb_owner";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // 2. The single instance of the connection
    private static Connection connection = null;

    // Private constructor to prevent "new PostgresConnection()"
    private PostgresConnection() {}

    /**
     * Gets the active database connection.
     * If one doesn't exist, it creates it.
     */
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                if (PASSWORD == null || PASSWORD.isEmpty()) {
                    System.err.println("❌ Error: DB_PASSWORD environment variable is not set.");
                }

                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Successfully connected to PostgreSQL!");
            }
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