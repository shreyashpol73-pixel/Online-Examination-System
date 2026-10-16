package com.examportal.dao;

import com.examportal.db.DatabaseConnection;
import com.examportal.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User entity.
 * Handles authentication, registration, and user retrieval operations.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Authenticates a user with given username and password.
     *
     * @param username user login identifier
     * @param password user password
     * @return User object if authentication is successful; null otherwise
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password, full_name, email, role, created_at " +
                     "FROM users WHERE username = ? AND password = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for authentication.");
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error authenticating user: " + username, e);
        }
        return null;
    }

    /**
     * Registers a new user into the database.
     *
     * @param user User object containing registration details
     * @return true if user was successfully registered; false otherwise
     */
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for user registration.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole() != null ? user.getRole() : "STUDENT");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + user.getUsername(), e);
        }
        return false;
    }

    /**
     * Retrieves a user by their unique user ID.
     *
     * @param userId primary key of the user
     * @return User object if found; null otherwise
     */
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password, full_name, email, role, created_at " +
                     "FROM users WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getUserById.");
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user with ID: " + userId, e);
        }
        return null;
    }

    /**
     * Retrieves all users having the role 'STUDENT'.
     *
     * @return List of User objects representing students
     */
    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        String sql = "SELECT user_id, username, password, full_name, email, role, created_at " +
                     "FROM users WHERE role = 'STUDENT' ORDER BY full_name ASC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getAllStudents.");
            return students;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving student list", e);
        }
        return students;
    }

    /**
     * Checks if a given username is already registered.
     *
     * @param username the username to check
     * @return true if username is already taken; false otherwise
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for isUsernameTaken.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking username existence: " + username, e);
        }
        return false;
    }

    /**
     * Checks if a given email is already registered.
     *
     * @param email the email address to check
     * @return true if email is already taken; false otherwise
     */
    public boolean isEmailTaken(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for isEmailTaken.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence: " + email, e);
        }
        return false;
    }

    /**
     * Helper method to map a ResultSet row to a User entity.
     *
     * @param rs active ResultSet positioned at the desired record
     * @return populated User object
     * @throws SQLException on database column read error
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
