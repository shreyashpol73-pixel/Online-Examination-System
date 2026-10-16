package com.examportal;

import com.examportal.db.DatabaseConnection;
import com.examportal.ui.LoginFrame;

import javax.swing.*;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application entry point for the Online Examination System.
 * Sets the system Look and Feel, verifies database connectivity,
 * and launches the Login interface on the Swing Event Dispatch Thread (EDT).
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // 1. Set System Look and Feel for native operating system controls
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to apply system look and feel", e);
        }

        // 2. Test Database Connectivity asynchronously or prior to showing UI
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                if (conn == null) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to connect to the database!\n\n" +
                            "Please ensure:\n" +
                            "1. MySQL Server is running on port 3306.\n" +
                            "2. The database 'online_exam_db' has been created.\n" +
                            "3. Database user 'root' credentials are valid.\n\n" +
                            "The application will launch, but database operations may fail until MySQL is started.",
                            "Database Connection Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    LOGGER.info("Initial database connection test succeeded.");
                }
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Database connection test threw an exception", ex);
                JOptionPane.showMessageDialog(null,
                        "Database connection error: " + ex.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }

            // 3. Launch Login Frame on EDT
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
