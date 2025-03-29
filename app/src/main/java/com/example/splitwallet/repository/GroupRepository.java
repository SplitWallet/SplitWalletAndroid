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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepository {
    private ApiService apiService;

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
}