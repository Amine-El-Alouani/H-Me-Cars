package com.h_me.carsapp.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * High-performance database connection manager using HikariCP connection pooling.
 * 
 * Benefits over single static connection:
 * - Connection reuse: Eliminates connection setup overhead
 * - Concurrent access: Multiple threads can get connections simultaneously
 * - Automatic recovery: Dead connections are detected and replaced
 * - Resource management: Connections are properly returned to pool
 */
public class PostgresConnection {

    private static final String URL = "jdbc:postgresql://ep-rough-hat-ag8k4p8x-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_YGyx47CvOmsf";

    private static HikariDataSource dataSource;
    private static boolean initialized = false;

    private PostgresConnection() {}

    /**
     * Initialize the connection pool. Called lazily on first getConnection().
     */
    private static synchronized void initializePool() {
        if (initialized) return;
        
        try {
            HikariConfig config = new HikariConfig();
            
            // Basic connection settings
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");
            
            // Pool sizing - optimized for desktop app with remote DB
            config.setMaximumPoolSize(10);      // Max concurrent connections
            config.setMinimumIdle(2);           // Keep 2 connections warm
            config.setIdleTimeout(300000);      // 5 minutes idle before close
            config.setMaxLifetime(1800000);     // 30 minutes max lifetime
            
            // Connection validation
            config.setConnectionTimeout(10000); // 10 seconds to get connection
            config.setValidationTimeout(5000);  // 5 seconds to validate
            config.setConnectionTestQuery("SELECT 1");
            
            // Performance optimizations for PostgreSQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            // Pool name for logging
            config.setPoolName("HMeCarsPool");
            
            dataSource = new HikariDataSource(config);
            initialized = true;
            
            System.out.println("✓ HikariCP connection pool initialized successfully!");
            System.out.println("  Pool size: " + config.getMaximumPoolSize() + " max, " + config.getMinimumIdle() + " min idle");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection from the pool.
     * 
     * IMPORTANT: Unlike before, connections from the pool SHOULD be closed
     * after use. Closing returns them to the pool, not actually closing them.
     * Use try-with-resources for automatic cleanup.
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initializePool();
        }
        
        if (dataSource == null) {
            throw new SQLException("Connection pool not initialized");
        }
        
        return dataSource.getConnection();
    }

    /**
     * Closes the entire connection pool. Call this when the app shuts down.
     */
    public static void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔌 Connection pool closed.");
            initialized = false;
        }
    }
    
    /**
     * Get pool statistics for debugging
     */
    public static String getPoolStats() {
        if (dataSource == null) {
            return "Pool not initialized";
        }
        return String.format("Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
    }

    public static void main(String[] args) {
        System.out.println("Testing HikariCP connection pool...");

        try {
            Connection testConn = PostgresConnection.getConnection();
            if (testConn != null) {
                System.out.println("✓ Got connection from pool");
                System.out.println("  Stats: " + getPoolStats());
                testConn.close(); // Returns to pool
                System.out.println("✓ Connection returned to pool");
                System.out.println("  Stats: " + getPoolStats());
            }
            PostgresConnection.closeConnection();
        } catch (SQLException e) {
            System.err.println("✗ Test failed: " + e.getMessage());
        }
    }
}