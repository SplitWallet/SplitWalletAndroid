package com.example.splitwallet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.repository.UserRepository;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

    public LiveData<JWTtoken> getTokenLiveData() {
        return tokenLiveData;
    }

    private final UserRepository userRepository = new UserRepository();

    public void login(String login, String password) {
        userRepository.login(login, password, tokenLiveData);
    }
}