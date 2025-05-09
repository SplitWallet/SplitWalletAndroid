package com.example.splitwallet;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.repository.UserRepository;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Mock ApiService apiService;
    @Mock Call<JWTtoken> call;

    @InjectMocks
    UserRepository userRepository;

    @Captor ArgumentCaptor<Callback<JWTtoken>> captor;

    private MutableLiveData<JWTtoken> liveData;

    @Before
    public void setup() {
        liveData = new MutableLiveData<>();
    }

    @Test
    public void login_successful() {
        when(apiService.login(any())).thenReturn(call);
        userRepository.login("user", "pass", liveData);
        verify(call).enqueue(captor.capture());

        JWTtoken token = new JWTtoken();
        Response<JWTtoken> response = Response.success(token);
        captor.getValue().onResponse(call, response);

        assertEquals(token, liveData.getValue());
    }

    @Test
    public void login_failed() {
        when(apiService.login(any())).thenReturn(call);
        userRepository.login("user", "pass", liveData);
        verify(call).enqueue(captor.capture());

        Response<JWTtoken> response = Response.error(401, ResponseBody.create(null, ""));
        captor.getValue().onResponse(call, response);

        assertNull(liveData.getValue());
    }

    @Test
    public void login_networkError() {
        when(apiService.login(any())).thenReturn(call);
        userRepository.login("user", "pass", liveData);
        verify(call).enqueue(captor.capture());

        captor.getValue().onFailure(call, new IOException("network error"));

        assertNull(liveData.getValue());
    }
}
