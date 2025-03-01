package com.example.splitwallet.api;


import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<User> login(@Body LoginRequest loginRequest);

    @POST("registration")
    Call<User> register(@Body RegisterRequest registerRequest);
}