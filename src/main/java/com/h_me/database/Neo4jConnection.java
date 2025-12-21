package com.h_me.database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Neo4jConnection {
    // 1. Create a static logger (Better than System.out/err)
    private static final Logger LOGGER = Logger.getLogger(Neo4jConnection.class.getName());

    public static void main(String[] args) {
        String uri = "neo4j+s://f4af5ebe.databases.neo4j.io";
        String user = "neo4j";
        String password = "Q_yHMPUVsdfti24_CgskB_U0BDCCJp1Fv6lzky936Pc";

        try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {

            try (Session session = driver.session()) {

                String greeting = session.run("RETURN 'Hello, Neo4j!' AS message")
                        .single()
                        .get("message")
                        .asString();

                LOGGER.info("Received from Database: " + greeting);
            }

        } catch (Exception e) {
            // 2. Log the exception properly with a severity level
            LOGGER.log(Level.SEVERE, "Database connection failed", e);
        }
    }
}