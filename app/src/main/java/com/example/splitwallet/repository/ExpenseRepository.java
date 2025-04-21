package com.example.splitwallet.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpenseCallback;
import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.models.ExpensesCallback;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.UpdateExpenseRequest;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpenseRepository {
    private final ApiService apiService;
    private final CurrencyConverter currencyConverter;


    public ExpenseRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        currencyConverter = new CurrencyConverter();
    }

    public void getExpenses(Long groupId, String token, ExpensesCallback callback) {
        Call<List<Expense>> call = apiService.getGroupExpenses(groupId, "Bearer " + token);
        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError("Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError("Unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createExpenseWithConversion(Long groupId, CreateExpenseRequest request,
                                            String token, MutableLiveData<Expense> result) {

        if ("RUB".equals(request.getCurrency())) {
            createExpense(groupId, request, token, result);
            return;
        }

        currencyConverter.convertToRub(groupId, request, token, new CurrencyConverter.ConversionCallback() {
            @Override
            public void onSuccess(CreateExpenseRequest rubRequest) {
                createExpense(groupId, rubRequest, token, result);
            }

            @Override
            public void onError(String error) {
                Log.e("CONVERSION", error);
                result.postValue(null);
            }
        });
    }

    public void createExpense(Long groupId, CreateExpenseRequest request, String token,
                              MutableLiveData<Expense> expenseLiveData) {

        Call<Expense> call = apiService.createExpense(groupId, "Bearer " + token, request);
        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                Log.d("RAW_RESPONSE", response.raw().toString());
                if (response.isSuccessful()) {
                    expenseLiveData.setValue(response.body());
                } else {
                    try {

                        Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Log.e("API_FAILURE", "Network error: ", t);
                expenseLiveData.setValue(null);
            }
        });
    }

    public void updateExpense(Long groupId, Long expenseId,
                              UpdateExpenseRequest request, String token,
                              ExpenseCallback callback) {
        Call<Expense> call = apiService.updateExpense(groupId, expenseId, "Bearer " + token, request);
        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError("Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError("Unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteExpense(Long groupId, Long expenseId, String token,
                              ExpenseCallback callback) {
        Call<Void> call = apiService.deleteExpense(groupId, expenseId, "Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Создаем пустой expense для колбэка
                    callback.onSuccess(new Expense());
                } else {
                    try {
                        callback.onError("Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError("Unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    public void getExpenseUsers(Long groupId, Long expenseId, String token,
                                Callback<List<ExpenseUser>> callback) {
        apiService.getExpenseUsers(groupId, expenseId, "Bearer " + token)
                .enqueue(callback);
    }

    public void updateExpenseUsers(Long groupId, Long expenseId, String token,
                                   List<ExpenseUser> expenseUsers, Callback<List<ExpenseUser>> callback) {
        apiService.updateExpenseUsers(groupId, expenseId, "Bearer " + token, expenseUsers)
                .enqueue(callback);
    }

    public void removeUserFromExpense(Long groupId, Long expenseId, String userId,
                                      String token, Callback<Void> callback) {
        apiService.removeUserFromExpense(groupId, expenseId, userId, "Bearer " + token)
                .enqueue(callback);
    }
    public interface ExpensesCallback {
        void onSuccess(List<Expense> expenses);

        void onError(String error);
    }

    public interface ExpenseCallback {
        void onSuccess(Expense expense);

        void onError(String error);
    }
}