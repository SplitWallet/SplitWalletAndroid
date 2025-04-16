package com.example.splitwallet.models;

public interface ExpenseCallback {
    void onSuccess(Expense expense);
    void onError(String error);
}