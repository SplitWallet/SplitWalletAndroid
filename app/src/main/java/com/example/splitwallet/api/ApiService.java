package com.example.splitwallet.api;


import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.models.UpdateExpenseRequest;
import com.example.splitwallet.models.User;
import com.example.splitwallet.models.UserResponse;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login")
    Call<JWTtoken> login(@Body LoginRequest loginRequest);

    @POST("registration")
    Call<JWTtoken> register(@Body RegisterRequest registerRequest);

    @POST("groups")
    Call<Group> createGroup(
            @Header("Authorization") String authToken,
            @Body CreateGroupRequest createGroupRequest
    );

    @POST("groups/{groupId}/expenses")
    Call<Expense> createExpense(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken,
            @Body CreateExpenseRequest request
    );

    @GET("groups/my")
    Call<List<Group>> getUserGroups(
            @Header("Authorization") String authToken
    );

    @GET("groups/{groupId}/expenses")
    Call<List<Expense>> getGroupExpenses(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );

    @PUT("groups/{groupId}/expenses/{expenseId}")
    Call<Expense> updateExpense(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken,
            @Body UpdateExpenseRequest request
    );

    @DELETE("groups/{groupId}/expenses/{expenseId}")
    Call<Void> deleteExpense(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken
    );

    @GET("groups/{groupId}/members")
    Call<List<User>> getGroupMembers(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );

    @POST("/groups/{uniqueCode}/join")
    Call<Void> joinGroup(
            @Path("uniqueCode") String uniqueCode,
            @Header("Authorization") String authToken
    );

    @GET("groups/{groupId}/expenses/{expenseId}/users")
    Call<List<ExpenseUser>> getExpenseUsers(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken
    );

    @PUT("groups/{groupId}/expenses/{expenseId}/users")
    Call<List<ExpenseUser>> updateExpenseUsers(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken,
            @Body List<ExpenseUser> expenseUsers
    );

    @DELETE("groups/{groupId}/expenses/{expenseId}/users/{userId}")
    Call<Void> removeUserFromExpense(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Path("userId") String userId,
            @Header("Authorization") String authToken
    );

    @DELETE("groups/{id}")
    Call<Void> deleteGroup(
            @Header("Authorization") String token,
            @Path("id") Long groupId
    );

    @DELETE("groups/{groupId}/members/{userId}")
    Call<Void> leaveGroup(
            @Path("groupId") Long groupId,
            @Path("userId") String userId,
            @Header("Authorization") String token
    );
}
