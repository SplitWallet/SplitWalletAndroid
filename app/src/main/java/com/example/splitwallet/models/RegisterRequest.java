package com.example.splitwallet.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}