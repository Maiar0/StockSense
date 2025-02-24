package com.CS360.stocksense.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class SecureLoginResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("user")
    private User user;
    @SerializedName("user_metadata")
    private Map<String, Object> user_metadata;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public User getUser(){return user;}
    public Map<String, Object> getUserMetadata() {
        if (user != null) {
            return user.getUserMetadata();
        }
        return null;
    }
    public String getOrganizationId() {
        if (user != null && user.getUserMetadata() != null && user.getUserMetadata().containsKey("organization_id")) {
            return user.getUserMetadata().get("organization_id").toString();
        }
        return null;
    }
    // Define the User class inside SecureLoginResponse
    public static class User {
        private Map<String, Object> user_metadata;

        public Map<String, Object> getUserMetadata() { return user_metadata; }
    }

}
