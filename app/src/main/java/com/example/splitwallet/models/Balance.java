package com.example.splitwallet.models;

import lombok.Getter;

@Getter
public class Balance {
    private String userId;
    private String username;
    private double youOwe;
    private double owesYou;
    private double netBalance;
}