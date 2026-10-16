package com.examportal.model;

import java.sql.Timestamp;

/**
 * Model class representing a User entity (Admin or Student) in the system.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role; // "ADMIN" or "STUDENT"
    private Timestamp createdAt;

    /**
     * Default no-arg constructor.
     */
    public User() {
    }

    /**
     * Parameterized constructor without userId and createdAt (typically used when registering a new user).
     *
     * @param username user login handle
     * @param password raw or hashed user password
     * @param fullName complete display name of the user
     * @param email unique email address
     * @param role role identifier ("ADMIN" or "STUDENT")
     */
    public User(String username, String password, String fullName, String email, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    /**
     * Full parameterized constructor.
     *
     * @param userId unique identifier in database
     * @param username user login handle
     * @param password user password
     * @param fullName complete display name of the user
     * @param email unique email address
     * @param role role identifier ("ADMIN" or "STUDENT")
     * @param createdAt timestamp of record creation
     */
    public User(int userId, String username, String password, String fullName, String email, String role, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
