package com.example.splitwallet.models;

import android.annotation.SuppressLint;

import java.time.LocalDateTime;
import java.util.List;

public class Group {
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User userOwner;
    private List<User> members;
    private List<Expense> events;
    private boolean isClosed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @SuppressLint("NewApi")
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @SuppressLint("NewApi")
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUserOwner() {
        return userOwner;
    }

    public void setUserOwner(User userOwner) {
        this.userOwner = userOwner;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<Expense> getEvents() {
        return events;
    }

    public void setEvents(List<Expense> events) {
        this.events = events;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}