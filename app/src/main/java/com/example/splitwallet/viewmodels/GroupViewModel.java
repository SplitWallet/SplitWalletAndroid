package com.example.splitwallet.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.User;
import com.example.splitwallet.repository.GroupRepository;
import com.example.splitwallet.models.UserResponse;

import java.util.List;

public class GroupViewModel extends ViewModel {
    private GroupRepository groupRepository = new GroupRepository();
    public MutableLiveData<Group> groupLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Group>> userGroupsLiveData = new MutableLiveData<>();
    // Добавляем LiveData для токена
    private MutableLiveData<JWTtoken> tokenLiveData;

    private MutableLiveData<List<UserResponse>> groupMembersLiveData = new MutableLiveData<>();

    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<List<Group>> getUserGroupsLiveData() {
        return userGroupsLiveData;
    }

    public LiveData<List<UserResponse>> getGroupMembersLiveData() {
        return groupMembersLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    public void loadUserGroups(String token) {
        groupRepository.getUserGroups(token, userGroupsLiveData);
    }

    public void loadGroupMembers(Long groupId, String token) {
        groupRepository.getGroupMembers(groupId, token, groupMembersLiveData);
    }

    // Метод для установки токена
    public void setToken(JWTtoken token) {
        if (tokenLiveData == null) {
            tokenLiveData = new MutableLiveData<>();
        }
        tokenLiveData.setValue(token);
    }

    public void createGroup(String name, String token) {
        if (token == null) {
            System.out.println("no token!!!");
            return;
        }
        groupRepository.createGroup(
                name,
                groupLiveData,
                token  // Получаем строку токена
        );
    }
}