package com.example.splitwallet.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class User {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    private String email;
    private String phoneNumber;

    public User() {}


    public User(String id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}