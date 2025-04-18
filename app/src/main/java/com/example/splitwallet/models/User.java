package com.example.splitwallet.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    private String email;
    private String phoneNumber;

    public User() {}


    public User(String id, String username, String email, String phoneNumber) {
        this.id = id;
        this.username = this.username;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}