package com.example.splitwallet.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.models.User;
import com.example.splitwallet.ui.RegisterActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;
    private Gson gson = new Gson();
    public UserRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void login(String login, String password, MutableLiveData<User> userLiveData) {
        Call<Object> call = apiService.login(new LoginRequest(login, password));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                String jsonResponse = gson.toJson(response.body());
                Log.d("API_RESPONSE", jsonResponse);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                    User user = gson.fromJson(jsonObject, User.class);
                    userLiveData.setValue(user);
                } else {
                    userLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                userLiveData.setValue(null);
            }
        });
    }

    public void register(String name, String email, String password, MutableLiveData<User> userLiveData) {
        Call<Object> call = apiService.register(new RegisterRequest(name, email, password));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                String jsonResponse = gson.toJson(response.body());
                Log.d("API_RESPONSE", jsonResponse);
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                    User user = gson.fromJson(jsonObject, User.class);
                    userLiveData.setValue(user);
                } else {
                    userLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                userLiveData.setValue(null);
            }
        });
    }
}