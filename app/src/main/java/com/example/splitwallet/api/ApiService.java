package com.example.splitwallet.api;


import com.example.splitwallet.models.Balance;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.models.GoogleLoginRequest;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.GroupBalancesResponse;
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
    @POST("auth-service/login")
    Call<JWTtoken> login(@Body LoginRequest loginRequest);

    @POST("auth-service/registration")
    Call<JWTtoken> register(@Body RegisterRequest registerRequest);

    @POST("auth-service/login/google")
    Call<JWTtoken> loginWithGoogle(@Body GoogleLoginRequest googleLoginRequest);

    @POST("groups-service/groups/create")
    Call<Group> createGroup(
            @Header("Authorization") String authToken,
            @Body CreateGroupRequest createGroupRequest
    );

    @POST("expenses-service/groups/{groupId}/expenses")
    Call<Expense> createExpense(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken,
            @Body CreateExpenseRequest request
    );

    @GET("groups-service/groups/my")
    Call<List<Group>> getUserGroups(
            @Header("Authorization") String authToken
    );

    @GET("expenses-service/groups/{groupId}/expenses")
    Call<List<Expense>> getGroupExpenses(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );

    @PUT("expenses-service/groups/{groupId}/expenses/{expenseId}")
    Call<Expense> updateExpense(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken,
            @Body UpdateExpenseRequest request
    );

    @DELETE("expenses-service/groups/{groupId}/expenses/{expenseId}")
    Call<Void> deleteExpense(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken
    );

    @GET("groups-service/groups/{groupId}")
    Call<Group> getGroupById(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );
    
    @GET("groups-service/groups/{groupId}/members")
    Call<List<User>> getGroupMembers(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );

    @POST("groups-service/groups/{uniqueCode}/join")
    Call<Void> joinGroup(
            @Path("uniqueCode") String uniqueCode,
            @Header("Authorization") String authToken
    );

    @GET("expensesuser-service/groups/{groupId}/expenses/{expenseId}")
    Call<List<ExpenseUser>> getExpenseUsers(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken
    );

    @PUT("expensesuser-service/groups/{groupId}/expenses/{expenseId}/users")
    Call<List<ExpenseUser>> updateExpenseUsers(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Header("Authorization") String authToken,
            @Body List<ExpenseUser> expenseUsers
    );

    @DELETE("expensesuser-service/groups/{groupId}/expenses/{expenseId}/users/{userId}")
    Call<Void> removeUserFromExpense(
            @Path("groupId") Long groupId,
            @Path("expenseId") Long expenseId,
            @Path("userId") String userId,
            @Header("Authorization") String authToken
    );

    @GET("expensesuser-service/group/{groupId}/debts")
    Call<GroupBalancesResponse> getGroupDebts(
            @Path("groupId") Long groupId,
            @Header("Authorization") String authToken
    );
    @DELETE("groups-service/groups/{id}")
    Call<Void> deleteGroup(
            @Header("Authorization") String token,
            @Path("id") Long groupId
    );

    @DELETE("groups-service/groups/{groupId}/members/{userId}")
    Call<Void> leaveGroup(
            @Path("groupId") Long groupId,
            @Path("userId") String userId,
            @Header("Authorization") String token
    );
}
