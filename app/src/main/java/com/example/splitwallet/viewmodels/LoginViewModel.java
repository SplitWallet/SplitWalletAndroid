package com.example.splitwallet.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.GoogleLoginRequest;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private final MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

    public LiveData<JWTtoken> getTokenLiveData() {
        return tokenLiveData;
    }

    private final UserRepository userRepository = new UserRepository();

    public void login(String login, String password) {
        userRepository.login(login, password, tokenLiveData);
    }
    public void loginWithGoogle(String idToken) {
        // Здесь нужно реализовать вызов API вашего сервера
        // для обмена Google ID токена на ваш JWT токен

        // Пример:
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<JWTtoken> call = apiService.loginWithGoogle(new GoogleLoginRequest(idToken));

        call.enqueue(new Callback<JWTtoken>() {
            @Override
            public void onResponse(Call<JWTtoken> call, Response<JWTtoken> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tokenLiveData.postValue(response.body());
                } else {
                    tokenLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<JWTtoken> call, Throwable t) {
                tokenLiveData.postValue(null);
            }
        });
    }
}