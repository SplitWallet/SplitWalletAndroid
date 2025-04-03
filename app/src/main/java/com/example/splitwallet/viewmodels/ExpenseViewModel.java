package com.example.splitwallet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.repository.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends ViewModel {
    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Expense> newExpenseLiveData = new MutableLiveData<>();

    public LiveData<List<Expense>> getExpensesLiveData() {
        return expensesLiveData;
    }

    public LiveData<Expense> getNewExpenseLiveData() {
        return newExpenseLiveData;
    }

    public void loadExpenses(Long groupId, String token) {
        expenseRepository.getExpenses(groupId, token, expensesLiveData);
    }

    public void createExpense(Long groupId, CreateExpenseRequest request, String token) {
        expenseRepository.createExpense(groupId, request, token, newExpenseLiveData);
    }
}