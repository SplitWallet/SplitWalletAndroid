package com.example.splitwallet.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.repository.GroupRepository;

public class GroupViewModel extends ViewModel {
    private GroupRepository groupRepository = new GroupRepository();
    public MutableLiveData<Group> groupLiveData = new MutableLiveData<>();

    // Добавляем LiveData для токена
    private MutableLiveData<JWTtoken> tokenLiveData;

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