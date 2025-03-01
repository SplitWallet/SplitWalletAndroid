package com.example.splitwallet.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.User;
import com.example.splitwallet.repository.UserRepository;

public class RegisterViewModel extends ViewModel {
    private UserRepository userRepository = new UserRepository();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public void register(String name, String email, String password) {
        userRepository.register(name, email, password, userLiveData);
    }
}