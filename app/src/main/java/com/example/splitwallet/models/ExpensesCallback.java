package com.example.splitwallet.models;

import java.util.List;

public interface ExpensesCallback {
    void onSuccess(List<Expense> expenses);
    void onError(String error);
}
