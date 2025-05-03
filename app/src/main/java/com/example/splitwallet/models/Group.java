package com.example.splitwallet.models;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class Group {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User userOwner;
    private List<User> members;
    private List<Expense> events;
    private boolean isClosed;
    private String uniqueCode;
}
