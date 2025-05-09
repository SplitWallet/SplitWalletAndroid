package com.example.splitwallet.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class JWTtoken {
    @SerializedName("token") // Для тестов
    private String jwtToken;

    public void setJwtToken(String testToken) {
        jwtToken = testToken;
    }
}
