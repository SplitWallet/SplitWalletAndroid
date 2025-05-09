package com.example.splitwallet.models;

public class GoogleLoginRequest {
    private String access_token;

    public GoogleLoginRequest(String googleToken) {
        this.access_token = googleToken;
    }

    public String getGoogleToken() {
        return access_token;
    }
}