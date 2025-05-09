package com.example.splitwallet;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.mockito.Mockito.*;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

public class ApiServiceTest {

    private MockWebServer mockWebServer;
    private ApiService apiService;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new com.example.splitwallet.models.LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new com.example.splitwallet.api.LocalDateTimeDeserializer())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void login_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"token\": \"abc123\"}").setResponseCode(200));

        Call<JWTtoken> call = apiService.login(new LoginRequest("user", "pass"));
        Response<JWTtoken> response = call.execute();

        assertTrue(response.isSuccessful());
        assertEquals("abc123", response.body().getJwtToken());
    }

    @Test
    public void register_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"token\": \"regtoken\"}").setResponseCode(200));

        Call<JWTtoken> call = apiService.register(new RegisterRequest("user", "pass", "email"));
        Response<JWTtoken> response = call.execute();

        assertTrue(response.isSuccessful());
        assertEquals("regtoken", response.body().getJwtToken());
    }

    @Test
    public void createGroup_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"id\": 1, \"name\": \"Trip\"}").setResponseCode(200));

        Call<Group> call = apiService.createGroup("Bearer token", new CreateGroupRequest("Trip"));
        Response<Group> response = call.execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("Trip", response.body().getName());
    }



    @Test
    public void getUserGroups_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("[{\"id\": 1, \"name\": \"Trip\"}]").setResponseCode(200));

        Call<List<Group>> call = apiService.getUserGroups("Bearer token");
        Response<List<Group>> response = call.execute();

        assertTrue(response.isSuccessful());
        assertEquals(1, response.body().size());
        assertEquals("Trip", response.body().get(0).getName());
    }

    @Test
    public void deleteGroup_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        Call<Void> call = apiService.deleteGroup("Bearer token", 1L);
        Response<Void> response = call.execute();

        assertTrue(response.isSuccessful());
        assertEquals(204, response.code());

    }

    @Test
    public void joinGroup_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        Call<Void> call = apiService.joinGroup("XYZ123", "Bearer token");
        Response<Void> response = call.execute();

        assertTrue(response.isSuccessful());
    }

    @Test
    public void createExpense_success() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\": 1, \"name\": \"Dinner\"}")
                .setResponseCode(200));

        CreateExpenseRequest req = new CreateExpenseRequest(
                "Dinner",
                LocalDate.now(),
                "Dinner at restaurant",
                100.0,
                "USD"
        );

        Call<Expense> call = apiService.createExpense(1L, "Bearer token", req);
        Response<Expense> response = call.execute();

        assertTrue(response.isSuccessful());
        assertEquals("Dinner", response.body().getName());
    }


    @Test
    public void getGroupDebts_success() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"balances\": []}").setResponseCode(200));

        Call<GroupBalancesResponse> call = apiService.getGroupDebts(1L, "Bearer token");
        Response<GroupBalancesResponse> response = call.execute();

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
    }


}
