package com.CS360.stocksense.models;

import androidx.annotation.NonNull;
import java.util.List;

/**
 * Represents an organization in the StockSense database.
 *
 * This model is used for querying and managing organization data within the database.
 * It contains fields such as organization, and password.
 *
 * Purpose:
 * - Provides a structure to encapsulate organization data.
 * - Offers getters and setters for accessing and modifying organization properties.
 *
 * Note:
 * - Field names are designed to match the database schema for compatibility.
 *
 * Example Usage:
 * ```java
 * Organization organization = new Organization();
 * organization.setOrganization("dennis");
 * organization.setPassword("hashed_password");
 * System.out.println(organization);
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
     * Constructor to initialize an organization object.
     *
     * @param organization The name of the organization.
     * @param password The organization's hashed password.
     * @param email The email associated with the organization.
     */
    public Organization(String organization, String password, String email) {
        this.organization = organization;
        this.password = password;
        this.email = email;
    }

    /**
     * Gets the organization name.
     *
     * @return The name of the organization.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the organization name.
     *
     * @param organization The name to assign to the organization.
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * Gets the organization's hashed password.
     *
     * @return The hashed password of the organization.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the organization's hashed password.
     *
     * @param password The hashed password to assign to the organization.
     */
    public void setPassword(String password) {
        this.password = password;
    }

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
    /**
     * Returns a string representation of the organization.
     *
     * This method provides a readable format of the organization's details
     * while ensuring that sensitive information, such as the password, is not exposed.
     *
     * @return A formatted string containing the organization's name and email,
     *         with the password field marked as "[PROTECTED]".
     */
    @NonNull
    @Override
    public String toString() {
        return "Organization{" +
                "organization='" + organization + '\'' +
                ", password='[PROTECTED]'" +
                ", email=" + email +
                '}';
    }
}
