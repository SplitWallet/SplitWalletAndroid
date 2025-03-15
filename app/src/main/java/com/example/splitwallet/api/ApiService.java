package com.example.splitwallet.api;


import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<Object> login(@Body LoginRequest loginRequest);

    @POST("registration")
    Call<Object> register(@Body RegisterRequest registerRequest);
    @POST("groups")
    Call<Group> createGroup(@Body CreateGroupRequest createGroupRequest);
}