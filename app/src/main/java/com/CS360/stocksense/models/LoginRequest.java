package com.CS360.stocksense.models;

import androidx.annotation.NonNull;

/**
 * Represents a secure login request sent to Supabase for validation.
 *
 * This model is used for authenticating users by sending their organization and hashed password.
 * It ensures that credentials are securely processed and validated.
 *
 * Purpose:
 * - Encapsulates login request data.
 * - Provides getters and setters for structured authentication.
 *
 * Note:
 * - Passwords should be **hashed before sending** to maintain security.
 * - This class follows the same format as `Item.java` for consistency.
 *
 * Example Usage:
 * ```java
 * LoginRequest loginRequest = new LoginRequest("organization_name", "hashed_password");
 * System.out.println(loginRequest);
 * ```
 *
 * @author Dennis Ward II
 * @version 1.1
 * @since 02/02/2025
 */
public class LoginRequest {
    private String organization_input;
    private String password_input;

    /**
     * Constructor to initialize a login request.
     *
     * @param organization      The user's username.
     * @param hashedPassword The hashed password for authentication.
     */
    public LoginRequest(String organization, String hashedPassword) {
        this.organization_input = organization;
        this.password_input = hashedPassword;
    }

    /**
     * Gets the username.
     *
     * @return The username.
     */
    public String getOrganizationInput() {
        return organization_input;
    }

    /**
     * Sets the organization name.
     *
     * @param organization_input The username to assign.
     */
    public void setOrganizationInput(String organization_input) {
        this.organization_input = organization_input;
    }

    /**
     * Gets the hashed password.
     *
     * @return The hashed password.
     */
    public String getHashedPassword() {
        return password_input;
    }

    /**
     * Sets the hashed password.
     *
     * @param hashedPassword The hashed password to assign.
     */
    public void setHashedPassword(String hashedPassword) {
        this.password_input = hashedPassword;
    }

    /**
     * Returns a string representation of the login request.
     *
     * @return A string with login request details.
     */
    @NonNull
    @Override
    public String toString() {
        return "LoginRequest{" +
                "organization='" + organization_input + '\'' +
                ", hashed_password=[Protected]" +
                '}';
    }
}
