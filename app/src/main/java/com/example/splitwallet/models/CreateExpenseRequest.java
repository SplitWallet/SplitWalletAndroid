package com.example.splitwallet.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;

// CreateExpenseRequest.java
@AllArgsConstructor
public class CreateExpenseRequest {
    private String name;
    private LocalDate date;
    private String description;
    private Double amount;
    private String currency;
}
