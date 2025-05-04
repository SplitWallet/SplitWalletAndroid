package com.example.splitwallet;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.JWTtoken;
import com.example.splitwallet.models.LoginRequest;
import com.example.splitwallet.models.RegisterRequest;
import com.example.splitwallet.repository.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MockWebServer mockWebServer;
    private UserRepository userRepository;
    private ApiService apiService;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        userRepository = new UserRepository(apiService);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private <T> T getOrAwaitValue(MutableLiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        liveData.observeForever(value -> {
            data[0] = value;
            latch.countDown();
        });

        latch.await(2, TimeUnit.SECONDS);
        return (T) data[0];
    }

    // Тест для успешного входа (login)

    @Test
    public void login_success_shouldReturnToken() throws Exception {
        // Arrange
        String successResponse = "{\"token\":\"test_token\",\"expiresIn\":3600}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(successResponse));

        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.login("test@example.com", "password", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNotNull(result);
        assertEquals("test_token", result.getJwtToken());

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/auth-service/login", request.getPath());
        assertTrue(request.getBody().readUtf8().contains("\"login\":\"test@example.com\""));
    }

    // Тест для ошибки входа

    @Test
    public void login_failure_shouldReturnNull() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Unauthorized\"}"));

        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.login("wrong@example.com", "wrong", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNull(result);
    }

    // Тест для сетевой ошибки при входе

    @Test
    public void login_networkError_shouldReturnNull() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.login("test@example.com", "password", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNull(result);
    }

    // Тест для успешной регистрации

    @Test
    public void register_success_shouldReturnToken() throws Exception {
        // Arrange
        String successResponse = "{\"token\":\"new_user_token\",\"expiresIn\":3600}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(successResponse));

        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.register("New User", "new@example.com", "password", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNotNull(result);
        assertEquals("new_user_token", result.getJwtToken());

        // Verify request
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/auth-service/registration", request.getPath());
        assertTrue(request.getBody().readUtf8().contains("\"email\":\"new@example.com\""));
    }

    // Тест для ошибки регистрации

    @Test
    public void register_emailExists_shouldReturnNull() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Email already exists\"}"));

        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.register("Existing User", "exists@example.com", "password", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNull(result);
    }

    // Тест для проверки обработки неверного JSON

    @Test
    public void login_invalidJsonResponse_shouldReturnNull() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"invalid\":json}"));

        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.login("test@example.com", "password", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNull(result);
    }

    // Тест для проверки таймаута

    @Test
    public void login_timeout_shouldReturnNull() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"token\":\"test\"}")
                .setBodyDelay(3, TimeUnit.SECONDS)); // Имитация таймаута

        MutableLiveData<JWTtoken> liveData = new MutableLiveData<>();

        // Act
        userRepository.login("test@example.com", "password", liveData);

        // Assert
        JWTtoken result = getOrAwaitValue(liveData);
        assertNull(result);
    }

    // Проверка заголовков запроса

    @Test
    public void requests_shouldHaveCorrectHeaders() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        // Act
        userRepository.login("test@example.com", "password", new MutableLiveData<>());

        // Assert
        RecordedRequest request = mockWebServer.takeRequest();

        // Проверка, что Content-Type начинается с "application/json" (с учетом возможной кодировки)
        assertTrue(request.getHeader("Content-Type").startsWith("application/json"));

        // Проверка, что заголовок Accept не равен null
        //assertNotNull(request.getHeader("Accept"));
    }



}