package com.example.splitwallet.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.splitwallet.models.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepository {
    private ApiService apiService;

    public GroupRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    private MutableLiveData<List<UserResponse>> groupMembersLiveData = new MutableLiveData<>();

    public void createGroup(String name, MutableLiveData<Group> groupLiveData, String token) {

        Call<Group> call = apiService.createGroup("Bearer " + token, new CreateGroupRequest(name));
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.isSuccessful()) {
                    groupLiveData.setValue(response.body());
                } else {
                    groupLiveData.setValue(null);
                    Log.e("API_ERROR", "Ошибка при создании группы: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.e("API_FAILURE", "Ошибка сети: ", t);
                groupLiveData.setValue(null);
            }
        });
    }

    public void getUserGroups(String token, MutableLiveData<List<Group>> groupsLiveData) {
        Call<List<Group>> call = apiService.getUserGroups("Bearer " + token);
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful()) {
                    List<Group> groups = response.body();
                    if (groups != null) {
                        groups.sort((g1, g2) -> g2.getUpdatedAt().compareTo(g1.getUpdatedAt()));
                    }
                    groupsLiveData.setValue(groups);
                } else {
                    Log.e("API_ERROR", "Failed to get groups: " + response.code());
                    groupsLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                Log.e("API_FAILURE", "Network error: ", t);
                groupsLiveData.setValue(null);
            }
        });
    }

    public void getGroupMembers(Long groupId, String token, MutableLiveData<List<UserResponse>> liveData) {
        apiService.getGroupMembers(groupId, token).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                liveData.postValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                liveData.postValue(null);
            }
        });
    }
}