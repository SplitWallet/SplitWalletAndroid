package com.example.splitwallet.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class JWTtoken {
    private String jwtToken;
    @SerializedName("token") // Для тестов
    public String getJwtToken()
    {
        return this.jwtToken;
    }
}
