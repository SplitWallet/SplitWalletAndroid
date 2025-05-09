package com.example.splitwallet.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Expense {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private LocalDate date;
    private String description;
    private Double amount;
    private String currency;
    private String userWhoCreatedId;
    private Boolean isActive;
    private Long groupId;
    private Double currentUserPaid;

}
