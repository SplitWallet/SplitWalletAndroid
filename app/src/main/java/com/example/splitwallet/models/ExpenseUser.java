package com.example.splitwallet.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseUser {
    private Long id;
    private String userId;
    private Long expenseId;
    private Double amount; // Сколько должен заплатить
    private Double paid;   // Сколько уже заплатил
}