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
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Expense> newExpenseLiveData = new MutableLiveData<>();


    public LiveData<Expense> getNewExpenseLiveData() {
        return newExpenseLiveData;
    }
    public LiveData<List<Expense>> getExpensesLiveData() {
        return expensesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void loadExpenses(Long groupId, String token) {
        expenseRepository.getExpenses(groupId, token, new ExpenseRepository.ExpensesCallback() {
            @Override
            public void onSuccess(List<Expense> expenses) {
                expenses.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));
                expensesLiveData.postValue(expenses);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    public void createExpense(Long groupId, CreateExpenseRequest request, String token) {
        expenseRepository.createExpense(groupId, request, token, newExpenseLiveData);
    }

    public void createExpenseWithConversion(Long groupId, CreateExpenseRequest request,
                                            String token){
        expenseRepository.createExpenseWithConversion(groupId, request, token, newExpenseLiveData);
    }
}