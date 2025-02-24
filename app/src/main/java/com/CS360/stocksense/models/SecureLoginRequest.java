package com.CS360.stocksense.models;

/**
 * SecureLoginRequest handles authentication requests securely by encapsulating user credentials.
 *
 * <p>
 * Features:
 * - Supports login via email and password.
 * - Allows authentication via a refresh token.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SecureLoginRequest {
    private String email;
    private String password;
    private String refresh_token;

    /**
     * Constructs a SecureLoginRequest for email-password authentication.
     *
     * @param email    The user's email.
     * @param password The user's password.
     */
    public SecureLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Constructs a SecureLoginRequest for refresh token authentication.
     *
     * @param refreshToken The refresh token for authentication.
     */
    public SecureLoginRequest(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    /**
     * Gets the email associated with the login request.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the password associated with the login request.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the refresh token used for authentication.
     *
     * @return The refresh token.
     */
    public String getRefreshToken() {
        return refresh_token;
    }
}