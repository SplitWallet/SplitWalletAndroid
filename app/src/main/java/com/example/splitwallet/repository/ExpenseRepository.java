package com.example.splitwallet.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseRepository {
    private final ApiService apiService;

    public ExpenseRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void getExpenses(Long groupId, String token, MutableLiveData<List<Expense>> expensesLiveData) {
        Call<List<Expense>> call = apiService.getGroupExpenses(groupId, "Bearer " + token);
        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if (response.isSuccessful()) {
                    expensesLiveData.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Failed to get expenses: " + response.code());
                    expensesLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Log.e("API_FAILURE", "Network error: ", t);
                expensesLiveData.setValue(null);
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
                    }                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Log.e("API_FAILURE", "Network error: ", t);
                expenseLiveData.setValue(null);
            }
        });
    }
}