package com.example.splitwallet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.models.ExpensesCallback;
import com.example.splitwallet.models.UpdateExpenseRequest;
import com.example.splitwallet.models.User;
import com.example.splitwallet.repository.ExpenseRepository;
import com.example.splitwallet.repository.GroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseViewModel extends ViewModel {
    private final ExpenseRepository expenseRepository = new ExpenseRepository();
    private final MutableLiveData<List<Expense>> expensesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Expense> newExpenseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, User>> groupMembersMap = new MutableLiveData<>();
    private final GroupRepository groupRepository = new GroupRepository();
    private MutableLiveData<List<ExpenseUser>> expenseUsersLiveData = new MutableLiveData<>();


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
    public LiveData<List<ExpenseUser>> getExpenseUsersLiveData() {
        return expenseUsersLiveData;
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
                              UpdateExpenseRequest request, String token,
                              List<ExpenseUser> participants) { // Добавьте participants

        // Проверяем сумму распределений
        double sumParticipants = participants.stream()
                .mapToDouble(ExpenseUser::getAmount)
                .sum();

        if (request.getAmount() < sumParticipants) {
            errorLiveData.postValue("Сумма расходов (" + request.getAmount() +
                    ") меньше суммы распределений (" + sumParticipants + ")");
            return;
        }

        expenseRepository.updateExpense(groupId, expenseId, request, token,
                new ExpenseRepository.ExpenseCallback() {
                    @Override
                    public void onSuccess(Expense expense) {
                        loadExpenses(groupId, token); // Обновляем список
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

    public void loadExpenseUsers(Long groupId, Long expenseId, String token) {
        expenseRepository.getExpenseUsers(groupId, expenseId, token, new Callback<List<ExpenseUser>>() {
            @Override
            public void onResponse(Call<List<ExpenseUser>> call, Response<List<ExpenseUser>> response) {
                if (response.isSuccessful()) {
                    expenseUsersLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ExpenseUser>> call, Throwable t) {
                // Обработка ошибки
            }
        });
    }



    public void updateExpenseUsers(Long groupId, Long expenseId, String token, List<ExpenseUser> updatedDistribution) {
        for (ExpenseUser eu : updatedDistribution) {
            if (eu.getPaid() == null) {
                eu.setPaid(0.0);
            }
        }

        expenseRepository.updateExpenseUsers(groupId, expenseId, token, updatedDistribution,
                new Callback<List<ExpenseUser>>() {
                    @Override
                    public void onResponse(Call<List<ExpenseUser>> call, Response<List<ExpenseUser>> response) {
                        if (response.isSuccessful()) {
                            expenseUsersLiveData.postValue(response.body());
                        } else {
                            // Обработка ошибки
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ExpenseUser>> call, Throwable t) {
                        // Обработка ошибки
                    }
                });
    }
}