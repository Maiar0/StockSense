package com.CS360.stocksense.models;

public class SecureLoginRequest {
    private String email;
    private String password;
    private String refresh_token;

    public SecureLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public SecureLoginRequest(String refreshToken) {
        this.refresh_token = refreshToken;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRefreshToken() { return refresh_token; }
}
