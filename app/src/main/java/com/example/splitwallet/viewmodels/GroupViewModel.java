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

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

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
    @Getter
    private int lastLeaveGroupResponseCode = -1;

    public void loadUserGroups(String token) {
        groupRepository.getUserGroups(token, userGroupsLiveData);
    }

    public void loadGroupMembers(Long groupId, String token) {
        groupRepository.getGroupMembers(groupId, token, new GroupRepository.MembersCallback() {
            @Override
            public void onSuccess(List<User> members) {
                List<UserResponse> userResponses = convertUsersToResponses(members);
                groupMembersLiveData.postValue(userResponses);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }
    private List<UserResponse> convertUsersToResponses(List<User> users) {
        List<UserResponse> responses = new ArrayList<>();
        for (User user : users) {
            responses.add(new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhoneNumber()
            ));
        }
        return responses;
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

    private MutableLiveData<Boolean> groupDeleted = new MutableLiveData<>();
    public LiveData<Boolean> getGroupDeleted() {
        return groupDeleted;
    }

    public void deleteGroup(Long groupId, String token) {
        groupRepository.deleteGroup(groupId, token, groupDeleted);
    }

    private final MutableLiveData<Boolean> leftGroupLiveData = new MutableLiveData<>();
    public LiveData<Boolean> getLeftGroupLiveData() {
        return leftGroupLiveData;
    }

    public void leaveGroup(Long groupId, String userId, String token) {
        groupRepository.leaveGroup(groupId, userId, token, (success, code) -> {
            lastLeaveGroupResponseCode = code;
            leftGroupLiveData.postValue(success);
        });
    }

}