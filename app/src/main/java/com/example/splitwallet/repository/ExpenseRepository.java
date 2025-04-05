package com.example.splitwallet.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
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