package com.example.splitwallet.models;

import lombok.Getter;

@Getter
public class JWTtoken {
    private String jwtToken;

    public String getJwtToken()
    {
        return this.jwtToken;
    }
}
