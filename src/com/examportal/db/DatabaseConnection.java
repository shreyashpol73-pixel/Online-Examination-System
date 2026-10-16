package com.examportal.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class managing the MySQL JDBC connection for the Online Examination System.
 * Provides thread-safe connection retrieval and lifecycle management.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/online_exam_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "$pp@2977";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private static DatabaseConnection instance;
    private Connection connection;

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    /**
     * Private constructor initializing credentials and registering the JDBC driver.
     */
    private DatabaseConnection() {
        this.dbUrl = System.getProperty("db.url", System.getenv().getOrDefault("DB_URL", DEFAULT_URL));
        this.dbUser = System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", DEFAULT_USER));
        this.dbPassword = System.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", DEFAULT_PASSWORD));

        try {
            Class.forName(DRIVER_CLASS);
            LOGGER.info("MySQL JDBC Driver registered successfully.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to locate MySQL JDBC Driver: " + DRIVER_CLASS, e);
        }
    }

    /**
     * Retrieves the singleton instance of DatabaseConnection.
     *
     * @return singleton DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtains an active connection to the MySQL database.
     * Reopens the connection if closed or not yet established.
     *
     * @return active java.sql.Connection object, or null if connection fails
     */
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                LOGGER.info("Establishing new connection to database at " + dbUrl);
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                LOGGER.info("Database connection successfully established.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failure: " + e.getMessage(), e);
        }
        return connection;
    }

    /**
     * Closes the active database connection if open.
     */
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LOGGER.info("Database connection closed successfully.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection: " + e.getMessage(), e);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Optional configuration helper to override connection parameters at runtime.
     *
     * @param url database URL
     * @param user database username
     * @param password database password
     */
    public synchronized void configure(String url, String user, String password) {
        closeConnection();
        this.dbUrl = url;
        this.dbUser = user;
        this.dbPassword = password;
        LOGGER.info("Database credentials updated.");
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }
}
