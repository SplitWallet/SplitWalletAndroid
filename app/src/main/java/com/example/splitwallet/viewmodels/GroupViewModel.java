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
    private final GroupRepository groupRepository = new GroupRepository();

    // LiveData объекты
    public MutableLiveData<Group> groupLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Group>> userGroupsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<UserResponse>> groupMembersLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> groupDeleted = new MutableLiveData<>();
    private final MutableLiveData<Boolean> leftGroupLiveData = new MutableLiveData<>();
    private MutableLiveData<JWTtoken> tokenLiveData;

    @Getter
    private int lastLeaveGroupResponseCode = -1;
    // Добавляем поле для хранения последнего кода ответа
    @Getter
    private int lastDeleteResponseCode = -1;

    // Геттеры для LiveData
    public LiveData<List<Group>> getUserGroupsLiveData() {
        return userGroupsLiveData;
    }

    public LiveData<List<UserResponse>> getGroupMembersLiveData() {
        return groupMembersLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getGroupDeleted() {
        return groupDeleted;
    }

    public LiveData<Boolean> getLeftGroupLiveData() {
        return leftGroupLiveData;
    }

    // Методы работы с группами
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

    public void createGroup(String name, String token) {
        if (token == null) {
            Log.e("GroupViewModel", "No token provided for createGroup");
            return;
        }
        groupRepository.createGroup(name, groupLiveData, token);
    }

    public void resetDeleteStatus() {
        groupDeleted.setValue(null);
    }

//    public void deleteGroup(Long groupId, String token) {
//        groupRepository.deleteGroup(groupId, token, groupDeleted);
//    }

    public void deleteGroup(Long groupId, String token) {
        groupRepository.deleteGroup(groupId, token, new GroupRepository.DeleteCallback() {
            @Override
            public void onResponse(boolean success, int code) {
                lastDeleteResponseCode = code;
                groupDeleted.postValue(success);
            }
        });
    }

    public void leaveGroup(Long groupId, String userId, String token) {
        groupRepository.leaveGroup(groupId, userId, token, (success, code) -> {
            lastLeaveGroupResponseCode = code;
            leftGroupLiveData.postValue(success);
        });
    }

    // Вспомогательные методы
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

    public void setToken(JWTtoken token) {
        if (tokenLiveData == null) {
            tokenLiveData = new MutableLiveData<>();
        }
        tokenLiveData.setValue(token);
    }
}