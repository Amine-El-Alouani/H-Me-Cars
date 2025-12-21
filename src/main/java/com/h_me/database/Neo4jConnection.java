package com.h_me.database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

public class Neo4jConnection {
    public static void main(String[] args) {
        String uri = "neo4j+s://f4af5ebe.databases.neo4j.io";
        String user = "neo4j";
        String password = "Q_yHMPUVsdfti24_CgskB_U0BDCCJp1Fv6lzky936Pc";

        try (Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))) {

            try (Session session = driver.session()) {

                String greeting = session.run("RETURN 'Hello, Neo4j!' AS message")
                        .single()       // Get the first record
                        .get("message") // Get the value of "message"
                        .asString();    // Convert it to a Java String

                System.out.println(greeting);
            }

        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
