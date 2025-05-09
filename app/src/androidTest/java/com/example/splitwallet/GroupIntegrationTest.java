package com.example.splitwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.LocalDateTimeDeserializer;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.repository.GroupRepository;
import com.example.splitwallet.ui.JoinGroupActivity;
import com.example.splitwallet.viewmodels.GroupViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(AndroidJUnit4.class)
public class GroupIntegrationTest {
    private MockWebServer mockWebServer;
    private ApiService apiService;
    private GroupRepository groupRepository;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
        groupRepository = new GroupRepository(apiService);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testCreateGroupAndJoin() throws Exception {
        // 1. Подготовим мок-ответ для создания группы
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{ \"id\": 1, \"name\": \"Test Group\", \"uniqueCode\": \"INVITE123\" }"));

        // 2. Подготовим мок-ответ для присоединения к группе
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        // 3. Вызываем createGroup через репозиторий
        MutableLiveData<Group> liveData = new MutableLiveData<>();
        String token = "mock_token";
        groupRepository.createGroup("Test Group", liveData, token);

        // 4. Подождём, пока LiveData обновится (в бою использовать CountDownLatch)
        Thread.sleep(500);

        Group createdGroup = liveData.getValue();
        assertNotNull(createdGroup);
        assertEquals("Test Group", createdGroup.getName());
        assertEquals("INVITE123", createdGroup.getUniqueCode());

        // 5. Проверим, что запрос был отправлен правильно
        RecordedRequest createRequest = mockWebServer.takeRequest();
        assertEquals("/groups/groups/create", createRequest.getPath());
        assertEquals("Bearer " + token, createRequest.getHeader("Authorization"));

        // 6. Вызываем joinGroup через ApiService напрямую (или оберни это в репозиторий)
        Call<Void> joinCall = apiService.joinGroup("INVITE123", "Bearer " + token);
        Response<Void> joinResponse = joinCall.execute();
        assertTrue(joinResponse.isSuccessful());

        // 7. Проверим корректность запроса на присоединение
        RecordedRequest joinRequest = mockWebServer.takeRequest();
        assertEquals("/groups/groups/INVITE123/join", joinRequest.getPath());
        assertEquals("Bearer " + token, joinRequest.getHeader("Authorization"));
    }
}
