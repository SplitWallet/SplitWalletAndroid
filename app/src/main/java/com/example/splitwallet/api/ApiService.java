package com.example.splitwallet.api;


import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<JWTtoken> login(@Body LoginRequest loginRequest);

    @POST("registration")
    Call<JWTtoken> register(@Body RegisterRequest registerRequest);

    @POST("groups")
    Call<Group> createGroup(@Body CreateGroupRequest createGroupRequest);
}