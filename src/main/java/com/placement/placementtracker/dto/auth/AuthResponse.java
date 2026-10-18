package com.placement.placementtracker.dto.auth;

public class AuthResponse {

    private final String tokenType = "Bearer";
    private final String accessToken;
    private final String name;
    private final String email;

    public AuthResponse(String accessToken, String name, String email) {
        this.accessToken = accessToken;
        this.name = name;
        this.email = email;
    }

    public String getTokenType() { return tokenType; }
    public String getAccessToken() { return accessToken; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
