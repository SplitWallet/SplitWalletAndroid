package com.example.splitwallet.api;


import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login")
    Call<JWTtoken> login(@Body LoginRequest loginRequest);

    @POST("registration")
    Call<JWTtoken> register(@Body RegisterRequest registerRequest);

    @POST("groups")
    Call<Group> createGroup(@Header("Authorization") String authToken, @Body CreateGroupRequest createGroupRequest);

    @POST("groups/{groupId}/expenses")
    Call<Expense> createExpense(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken,
            @Body CreateExpenseRequest request
    );
    @GET("groups/my")
    Call<List<Group>> getUserGroups(@Header("Authorization") String authToken);

    @GET("groups/{groupId}/expenses")
    Call<List<Expense>> getGroupExpenses(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );
}
