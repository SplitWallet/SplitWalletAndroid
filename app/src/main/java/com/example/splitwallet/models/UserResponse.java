package com.example.splitwallet.models;

import lombok.Getter;

@Getter
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;

    public UserResponse(String id, String name, String email, String phoneNumber){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}