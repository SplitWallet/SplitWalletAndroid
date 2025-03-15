package com.example.splitwallet.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.User;
import com.example.splitwallet.repository.UserRepository;

public class LoginViewModel extends ViewModel {
    public MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

    private UserRepository userRepository = new UserRepository();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public void login(String login, String password) {
        userRepository.login(login, password, tokenLiveData);
    }
}