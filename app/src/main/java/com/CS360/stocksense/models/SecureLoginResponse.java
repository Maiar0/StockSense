package com.CS360.stocksense.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * SecureLoginResponse represents the response from a secure authentication request.
 *
 * <p>
 * Features:
 * - Contains access and refresh tokens for authentication.
 * - Holds user details, including metadata.
 * - Provides methods to retrieve organization ID from user metadata.
 * </p>
 *
 * @author Dennis Ward II
 * @version 1.0
 * @since 01/20/2025
 */
public class SecureLoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("user")
    private User user;

    @SerializedName("user_metadata")
    private Map<String, Object> user_metadata;

    /**
     * Gets the access token from the login response.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets the refresh token from the login response.
     *
     * @return The refresh token.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Gets the user object containing metadata.
     *
     * @return The user object.
     */
    public User getUser() {
        return user;
    }

    /**
     * Retrieves user metadata if available.
     *
     * @return A map containing user metadata, or null if unavailable.
     */
    public Map<String, Object> getUserMetadata() {
        if (user != null) {
            return user.getUserMetadata();
        }
        return null;
    }

    /**
     * Retrieves the organization ID from user metadata.
     *
     * @return The organization ID as a string, or null if unavailable.
     */
    public String getOrganizationId() {
        if (user != null && user.getUserMetadata() != null && user.getUserMetadata().containsKey("organization_id")) {
            return user.getUserMetadata().get("organization_id").toString();
        }
        return null;
    }

    /**
     * Inner User class representing user details with metadata.
     */
    public static class User {
        private Map<String, Object> user_metadata;

        /**
         * Retrieves the user metadata.
         *
         * @return A map containing user metadata.
         */
        public Map<String, Object> getUserMetadata() {
            return user_metadata;
        }
    }
}