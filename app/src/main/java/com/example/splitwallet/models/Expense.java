package com.example.splitwallet.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Expense {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String name;

    private LocalDate date;

    private String description;

    private Double amount;

    private String currency = "RUB";

    private User userWhoCreated;

    private Boolean isActive = true;

    private Group group;

    private List<ExpenseUser> expenseUsers;
}