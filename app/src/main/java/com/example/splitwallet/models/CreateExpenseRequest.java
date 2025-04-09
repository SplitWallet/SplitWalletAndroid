package com.example.splitwallet.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

// CreateExpenseRequest.java
@AllArgsConstructor

public class CreateExpenseRequest {
    private String name;
    private LocalDate date;
    private String description;
    private Double amount;
    private String currency;



    public Double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }
}