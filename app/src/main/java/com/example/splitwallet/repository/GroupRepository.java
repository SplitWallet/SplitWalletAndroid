package com.example.splitwallet.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.User;
import com.example.splitwallet.models.UserResponse;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepository {
    public interface MembersCallback {
        void onSuccess(List<User> members);
        void onError(String error);
    }

    public interface LeaveGroupCallback {
        void onResponse(boolean success, int code);
    }

    private final ApiService apiService;
    private final MutableLiveData<List<UserResponse>> groupMembersLiveData = new MutableLiveData<>();

    public GroupRepository() {
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

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

    public void getGroupMembers(Long groupId, String token, MembersCallback callback) {
        Call<List<User>> call = apiService.getGroupMembers(groupId, "Bearer " + token);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError("Error: " + response.errorBody().string());
                    } catch (IOException e) {
                        callback.onError("Unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Методы из второй версии
    public void deleteGroup(Long groupId, String token, MutableLiveData<Boolean> resultLiveData) {
        Call<Void> call = apiService.deleteGroup("Bearer " + token, groupId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                resultLiveData.postValue(response.isSuccessful());
                if (!response.isSuccessful()) {
                    Log.e("GroupRepository", "Delete failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("GroupRepository", "Delete error: " + t.getMessage());
                resultLiveData.postValue(false);
            }
        });
    }

    public void leaveGroup(Long groupId, String userId, String token, LeaveGroupCallback callback) {
        Call<Void> call = apiService.leaveGroup(groupId, userId, "Bearer " + token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                callback.onResponse(response.isSuccessful(), response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onResponse(false, -1);
            }
        });
    }
}