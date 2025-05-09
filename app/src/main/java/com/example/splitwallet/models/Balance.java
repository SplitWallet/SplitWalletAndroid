package com.example.splitwallet.models;

import lombok.Getter;

@Getter
public class Balance {
    private String userId;
    private String username;
    private double youOwe;
    private double owesYou;
    private double netBalance;

    // Для тестов

    public Balance(String username, double youOwe, double owesYou) {
        this.username = username;
        this.youOwe = youOwe;
        this.owesYou = owesYou;
        this.netBalance = owesYou - youOwe;
    }
}