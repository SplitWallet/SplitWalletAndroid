package com.example.splitwallet.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.models.JWTtoken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;
    private final Gson gson = new Gson();
    private static final String TAG = "UserRepository";

    // Для тестов
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }
    public UserRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void login(String login, String password, MutableLiveData<JWTtoken> tokenLiveData) {
        Call<JWTtoken> call = apiService.login(new LoginRequest(login, password));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JWTtoken> call, Response<JWTtoken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                    JWTtoken token = gson.fromJson(jsonObject, JWTtoken.class);
                    tokenLiveData.setValue(token);
                } else {
                    Log.e(TAG, "Login error: " + response.code() + " - " + response.message());
                    tokenLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<JWTtoken> call, Throwable t) {
                Log.e(TAG, "Login network error: " + t.getMessage(), t);
                tokenLiveData.setValue(null);
            }
        });
    }

    public void register(String name, String email, String password, MutableLiveData<JWTtoken> tokenLiveData) {
        Call<JWTtoken> call = apiService.register(new RegisterRequest(name, email, password));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<JWTtoken> call, Response<JWTtoken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                    JWTtoken token = gson.fromJson(jsonObject, JWTtoken.class);
                    tokenLiveData.setValue(token);
                } else {
                    Log.e(TAG, "Register error: " + response.code() + " - " + response.message());
                    tokenLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<JWTtoken> call, Throwable t) {
                Log.e(TAG, "Register network error: " + t.getMessage(), t);
                tokenLiveData.setValue(null);
            }
        });
    }
}