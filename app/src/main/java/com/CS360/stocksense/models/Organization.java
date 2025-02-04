package com.CS360.stocksense.models;

import androidx.annotation.NonNull;
import java.util.List;

/**
 * Represents a user in the StockSense database.
 *
 * This model is used for querying and managing user data within the database.
 * It contains fields such as username, password, and organizations.
 *
 * Purpose:
 * - Provides a structure to encapsulate user data.
 * - Offers getters and setters for accessing and modifying user properties.
 *
 * Note:
 * - Field names are designed to match the database schema for compatibility.
 *
 * Example Usage:
 * ```java
 * User user = new User();
 * user.setUsername("dennis");
 * user.setPassword("hashed_password");
 * System.out.println(user);
 * ```
 *
 * @author Dennis Ward II
 * @version 1.2
 * @since 02/02/2025
 */
public class Organization {
    private String organization;
    private String password; // Should store hashed passwords only
    private String email;

    /**
     * Constructor to initialize a User object.
     *
     * @param organization      The user's chosen username.
     * @param password      The user's hashed password.
     * @param email      The user's hashed password.
     */
    public Organization(String organization, String password, String email) {
        this.organization = organization;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the username.
     *
     * @return The username of the user.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the username.
     *
     * @param organization The username to assign to the user.
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * Gets the user's hashed password.
     *
     * @return The hashed password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's hashed password.
     *
     * @param password The hashed password to assign to the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return A string with the user's details.
     */
    /**
     * Gets the email associated with the organization.
     *
     * @return The email of the organization.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email for the organization.
     *
     * @param email The email to assign.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return "Organization{" +
                "organization='" + organization + '\'' +
                ", password='[PROTECTED]'" + // Avoid printing actual passwords
                ", email=" + email +
                '}';
    }
}
