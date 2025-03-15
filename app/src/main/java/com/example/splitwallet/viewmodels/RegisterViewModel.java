package com.example.splitwallet.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.User;
import com.example.splitwallet.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();
    public LiveData<JWTtoken> getTokenLiveData() {
        return tokenLiveData;
    }
    private UserRepository userRepository = new UserRepository();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public void register(String name, String email, String password) {
        userRepository.register(name, email, password, tokenLiveData);
    }
}