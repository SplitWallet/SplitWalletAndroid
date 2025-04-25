package com.example.splitwallet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpensesCallback;
import com.example.splitwallet.models.UpdateExpenseRequest;
import com.example.splitwallet.models.User;
import com.example.splitwallet.repository.ExpenseRepository;
import com.example.splitwallet.repository.GroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseViewModel extends ViewModel {
    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Expense> newExpenseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, User>> groupMembersMap = new MutableLiveData<>();
    private final GroupRepository groupRepository = new GroupRepository();


    public LiveData<Expense> getNewExpenseLiveData() {
        return newExpenseLiveData;
    }
    public MutableLiveData<Map<String, User>> getGroupMembersMap(){return groupMembersMap;}

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
    private Map<String, User> cachedMembersMap = new HashMap<>();
    public void loadExpensesWithMembers(Long groupId, String token) {
        if (!cachedMembersMap.isEmpty()) {
            groupMembersMap.postValue(cachedMembersMap);
            loadExpenses(groupId, token);
            return;
        }
        // Сначала загружаем участников группы
        groupRepository.getGroupMembers(groupId, token, new GroupRepository.MembersCallback() {
            @Override
            public void onSuccess(List<User> members) {
                // Заполняем карту участников
                Map<String, User> newMap = new HashMap<>();
                for (User user : members) {
                    newMap.put(user.getId(), user);
                }
                cachedMembersMap = newMap;
                groupMembersMap.postValue(newMap);

                // Затем загружаем расходы
                loadExpenses(groupId, token);
            }

            @Override
            public void onError(String error) {
                // Можно загрузить расходы даже без информации о пользователях
                groupMembersMap.postValue(new HashMap<>());
                loadExpenses(groupId, token);
                errorLiveData.postValue(error);
            }
        });
    }

    public void createExpense(Long groupId, CreateExpenseRequest request, String token) {
        expenseRepository.createExpense(groupId, request, token, newExpenseLiveData);
    }

    public void createExpenseWithConversion(Long groupId, CreateExpenseRequest request,
                                            String token) {
        expenseRepository.createExpenseWithConversion(groupId, request, token, newExpenseLiveData);
    }

    public void updateExpense(Long groupId, Long expenseId,
                              UpdateExpenseRequest request, String token) {
        expenseRepository.updateExpense(groupId, expenseId, request, token,
                new ExpenseRepository.ExpenseCallback() {
                    @Override
                    public void onSuccess(Expense expense) {
                        // Обновляем список расходов
                        loadExpenses(groupId, token);
                    }

                    @Override
                    public void onError(String error) {
                        errorLiveData.postValue(error);
                    }
                });
    }

    public void deleteExpense(Long groupId, Long expenseId, String token) {
        expenseRepository.deleteExpense(groupId, expenseId, token,
                new ExpenseRepository.ExpenseCallback() {
                    @Override
                    public void onSuccess(Expense expense) {
                        // Обновляем список расходов
                        loadExpenses(groupId, token);
                    }

                    @Override
                    public void onError(String error) {
                        errorLiveData.postValue(error);
                    }
                });
    }
}