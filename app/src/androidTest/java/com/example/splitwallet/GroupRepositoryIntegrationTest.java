package com.example.splitwallet;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.*;
import com.example.splitwallet.repository.GroupRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(JUnit4.class)
public class GroupRepositoryIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MockWebServer mockWebServer;
    private GroupRepository repository;
    private ApiService apiService;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                        LocalDateTime.parse(json.getAsString())
                )
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        repository = new GroupRepository(apiService);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    public static <T> T getOrAwaitValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);

        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };

        liveData.observeForever(observer);

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new RuntimeException("LiveData value was never set.");
        }

        return (T) data[0];
    }


    // Создание группы

    @Test
    public void createGroup_shouldUseCorrectEndpoint() throws Exception {
        // Arrange
        String responseJson = "{\"id\":1,\"name\":\"Test Group\"}";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson));

        MutableLiveData<Group> liveData = new MutableLiveData<>();

        // Act
        repository.createGroup("Test Group", liveData, "valid_token");

        // Assert
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/groups/groups/create", request.getPath());
        assertEquals("Bearer valid_token", request.getHeader("Authorization"));
        assertEquals("{\"name\":\"Test Group\"}", request.getBody().readUtf8());
    }

    // Получение групп пользователя

    @Test
    public void getUserGroups_shouldUseCorrectEndpoint() throws Exception {
        // Arrange
        String responseJson = "[{\"id\":1,\"name\":\"Group 1\"}]";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson));

        MutableLiveData<List<Group>> liveData = new MutableLiveData<>();

        // Act
        repository.getUserGroups("valid_token", liveData);

        // Assert
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/groups/groups/my", request.getPath());
        assertEquals("Bearer valid_token", request.getHeader("Authorization"));
    }

    // Удаление группы

    @Test
    public void deleteGroup_shouldUseCorrectEndpoint() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        CountDownLatch latch = new CountDownLatch(1);
        GroupRepository.DeleteCallback callback = new GroupRepository.DeleteCallback() {
            @Override
            public void onResponse(boolean success, int code) {
                assertTrue(success);
                assertEquals(204, code);
                latch.countDown();
            }
        };

        // Act
        repository.deleteGroup(1L, "valid_token", callback);

        // Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
        assertEquals("/groups/groups/1", request.getPath());
        assertEquals("Bearer valid_token", request.getHeader("Authorization"));
    }

    // Выход из группы

    @Test
    public void leaveGroup_shouldUseCorrectEndpoint() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        CountDownLatch latch = new CountDownLatch(1);
        GroupRepository.LeaveGroupCallback callback = new GroupRepository.LeaveGroupCallback() {
            @Override
            public void onResponse(boolean success, int code) {
                assertTrue(success);
                assertEquals(200, code);
                latch.countDown();
            }
        };

        // Act
        repository.leaveGroup(1L, "user123", "valid_token", callback);

        // Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
        assertEquals("/groups/groups/1/members/user123", request.getPath());
        assertEquals("Bearer valid_token", request.getHeader("Authorization"));
    }

    // Тесты для получения участников группы

@Test
public void getGroupMembers_shouldReturnCorrectUser() throws Exception {
    // 1. Готовим корректный JSON
    String responseJson = "[{\"id\":\"user1\", \"username\":\"User 1\"}]";
    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(responseJson));

    // 2. Создаем callback с проверками
    CountDownLatch latch = new CountDownLatch(1);
    GroupRepository.MembersCallback callback = new GroupRepository.MembersCallback() {
        @Override
        public void onSuccess(List<User> members) {
            try {
                assertNotNull("Members list should not be null", members);
                assertFalse("Members list should not be empty", members.isEmpty());
                assertEquals("User 1", members.get(0).getUsername());
            } finally {
                latch.countDown();
            }
        }

        @Override
        public void onError(String error) {
            fail("Should not call onError: " + error);
            latch.countDown();
        }
    };

    // 3. Вызываем метод
    repository.getGroupMembers(1L, "valid_token", callback);

    // 4. Ждем завершения и проверяем запрос
    assertTrue(latch.await(2, TimeUnit.SECONDS));
    RecordedRequest request = mockWebServer.takeRequest();
    assertEquals("/groups/groups/1/members", request.getPath());
}

    // Тесты для граничных случаев
    // Пустой список групп

    @Test
    public void getUserGroups_emptyResponse_shouldReturnEmptyList() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[]"));

        MutableLiveData<List<Group>> liveData = new MutableLiveData<>();

        // Act
        repository.getUserGroups("valid_token", liveData);

        // Assert
        List<Group> groups = getOrAwaitValue(liveData);
        assertNotNull(groups);
        assertTrue(groups.isEmpty());
    }

    // Неверный токен

    @Test
    public void createGroup_invalidToken_shouldReturnNull() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        MutableLiveData<Group> liveData = new MutableLiveData<>();

        // Act
        repository.createGroup("Test", liveData, "invalid_token");

        // Assert
        assertNull(getOrAwaitValue(liveData));
        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("Bearer invalid_token", request.getHeader("Authorization"));
    }

    // Тесты для сетевых ошибок

    @Test
    public void leaveGroup_networkError_shouldReturnFalse() throws Exception {
        // Arrange
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        CountDownLatch latch = new CountDownLatch(1);
        GroupRepository.LeaveGroupCallback callback = new GroupRepository.LeaveGroupCallback() {
            @Override
            public void onResponse(boolean success, int code) {
                assertFalse(success);
                assertEquals(-1, code);
                latch.countDown();
            }
        };

        // Act
        repository.leaveGroup(1L, "user123", "valid_token", callback);

        // Assert
        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

}
