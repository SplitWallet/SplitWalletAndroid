package com.example.splitwallet;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.LiveDataTestUtil;
import com.example.splitwallet.viewmodels.LoginViewModel;
import com.example.splitwallet.api.RetrofitClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class LoginViewModelIntegrationTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private MockWebServer server;
    private LoginViewModel viewModel;

    @Before
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();

        RetrofitClient.overrideBaseUrl(server.url("/").toString());
        viewModel = new LoginViewModel();
    }


    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void login_returnsTokenOnSuccess() throws Exception {
        String body = "{\"token\":\"jwt_token\"}";
        server.enqueue(new MockResponse().setResponseCode(200).setBody(body));

        viewModel.login("test@example.com", "password");

        JWTtoken token = LiveDataTestUtil.getOrAwaitValue(viewModel.getTokenLiveData());
        assertEquals("jwt_token", token.getJwtToken());
    }
}
