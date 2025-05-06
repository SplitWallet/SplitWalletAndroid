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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class UserRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiService apiService;

    @Mock
    private Call<JWTtoken> jwtCall;

    @Captor
    private ArgumentCaptor<Callback<JWTtoken>> jwtCallbackCaptor;

    private UserRepository userRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userRepository = new UserRepository(apiService);
    }

    @Test
    public void testLogin_success() {
        String login = "user";
        String password = "pass";
        MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

        JWTtoken fakeToken = new JWTtoken();
        fakeToken.setJwtToken("abc.def.ghi");

        when(apiService.login(any(LoginRequest.class))).thenReturn(jwtCall);

        userRepository.login(login, password, tokenLiveData);

        verify(jwtCall).enqueue(jwtCallbackCaptor.capture());

        jwtCallbackCaptor.getValue().onResponse(jwtCall, Response.success(fakeToken));

        assertNotNull(tokenLiveData.getValue());
        assertEquals("abc.def.ghi", tokenLiveData.getValue().getJwtToken());
    }

    @Test
    public void testLogin_failure_responseError() {
        MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

        when(apiService.login(any(LoginRequest.class))).thenReturn(jwtCall);

        userRepository.login("user", "pass", tokenLiveData);

        verify(jwtCall).enqueue(jwtCallbackCaptor.capture());

        jwtCallbackCaptor.getValue().onResponse(jwtCall,
                Response.error(401, okhttp3.ResponseBody.create(null, "Unauthorized")));

        assertNull(tokenLiveData.getValue());
    }

    @Test
    public void testLogin_failure_networkError() {
        MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

        when(apiService.login(any(LoginRequest.class))).thenReturn(jwtCall);

        userRepository.login("user", "pass", tokenLiveData);

        verify(jwtCall).enqueue(jwtCallbackCaptor.capture());

        jwtCallbackCaptor.getValue().onFailure(jwtCall, new Throwable("Network error"));

        assertNull(tokenLiveData.getValue());
    }

    @Test
    public void testRegister_success() {
        MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

        JWTtoken fakeToken = new JWTtoken();
        fakeToken.setJwtToken("xyz.abc.123");

        when(apiService.register(any(RegisterRequest.class))).thenReturn(jwtCall);

        userRepository.register("User", "user@example.com", "pass", tokenLiveData);

        verify(jwtCall).enqueue(jwtCallbackCaptor.capture());

        jwtCallbackCaptor.getValue().onResponse(jwtCall, Response.success(fakeToken));

        assertNotNull(tokenLiveData.getValue());
        assertEquals("xyz.abc.123", tokenLiveData.getValue().getJwtToken());
    }

    @Test
    public void testRegister_failure_responseError() {
        MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

        when(apiService.register(any(RegisterRequest.class))).thenReturn(jwtCall);

        userRepository.register("User", "user@example.com", "pass", tokenLiveData);

        verify(jwtCall).enqueue(jwtCallbackCaptor.capture());

        jwtCallbackCaptor.getValue().onResponse(jwtCall,
                Response.error(400, okhttp3.ResponseBody.create(null, "Bad Request")));

        assertNull(tokenLiveData.getValue());
    }

    @Test
    public void testRegister_failure_networkError() {
        MutableLiveData<JWTtoken> tokenLiveData = new MutableLiveData<>();

        when(apiService.register(any(RegisterRequest.class))).thenReturn(jwtCall);

        userRepository.register("User", "user@example.com", "pass", tokenLiveData);

        verify(jwtCall).enqueue(jwtCallbackCaptor.capture());

        jwtCallbackCaptor.getValue().onFailure(jwtCall, new Throwable("No internet"));

        assertNull(tokenLiveData.getValue());
    }
}
