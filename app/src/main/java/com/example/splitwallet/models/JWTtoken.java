package com.example.splitwallet.models;

public class JWTtoken {
    private String jwtToken;

    public JWTtoken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken(){
        return jwtToken;
    }

    public void setJwtToken(String jwtToken){
        this.jwtToken = jwtToken;
    }
}
