package com.example.splitwallet.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UpdateExpenseRequest {
    private String name;
    private LocalDate date;
    private String description;
    private Double amount;
    private String currency;
}