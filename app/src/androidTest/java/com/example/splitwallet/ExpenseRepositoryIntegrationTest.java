package com.example.splitwallet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.repository.ExpenseRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
@RunWith(MockitoJUnitRunner.class)
public class ExpenseRepositoryIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MockWebServer mockWebServer;
    private ExpenseRepository expenseRepository;

    @Mock
    private CurrencyConverter mockCurrencyConverter;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Создаём кастомный Gson с адаптером для LocalDate
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        // Создаём Retrofit с этим Gson
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(gson)) // 👈 используем кастомный gson
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Передаём в репозиторий нужные зависимости
        expenseRepository = new ExpenseRepository(apiService, mockCurrencyConverter);
    }


    @After
    public void tearDown() throws Exception {
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

    // Тест для создания расхода с полным набором данных

    @Test
    public void createExpense_shouldSendCorrectRequestWithAllFields() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2023, 5, 15);
        CreateExpenseRequest request = new CreateExpenseRequest(
                "Team Dinner",
                testDate,
                "Monthly team meeting",
                120.50,
                "USD"
        );

        String expectedJson = "{" +
                "\"name\":\"Team Dinner\"," +
                "\"date\":\"2023-05-15\"," +
                "\"description\":\"Monthly team meeting\"," +
                "\"amount\":120.5," +
                "\"currency\":\"USD\"" +
                "}";

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        MutableLiveData<Expense> liveData = new MutableLiveData<>();

        // Act
        expenseRepository.createExpense(1L, request, "valid_token", liveData);

        // Assert
        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        JsonElement expected = JsonParser.parseString(expectedJson);
        JsonElement actual = JsonParser.parseString(recordedRequest.getBody().readUtf8());

        assertEquals(expected, actual);  // сравниваются структуры, а не строки
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/expenses-service/groups/1/expenses", recordedRequest.getPath());
    }

    // Тест для создания расхода с минимальными данными

    @Test
    public void createExpense_shouldHandleMinimumRequiredFields() throws Exception {
        // Arrange
        CreateExpenseRequest request = new CreateExpenseRequest(
                "Taxi",
                null,
                null,
                25.0,
                "EUR"
        );

        String expectedJson = "{" +
                "\"name\":\"Taxi\"," +
                "\"amount\":25.0," +
                "\"currency\":\"EUR\"" +
                "}";

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        MutableLiveData<Expense> liveData = new MutableLiveData<>();

        // Act
        expenseRepository.createExpense(1L, request, "valid_token", liveData);

        // Assert
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        JSONAssert.assertEquals(expectedJson, recordedRequest.getBody().readUtf8(), false);
    }

    // Тест для проверки валютной конверсии с полями запроса

    @Test
    public void createExpenseWithConversion_shouldConvertCurrency() throws Exception {
        // Arrange
        CreateExpenseRequest originalRequest = new CreateExpenseRequest(
                "Dinner",
                LocalDate.now(),
                "Team dinner",
                50.0,
                "EUR"
        );

        CreateExpenseRequest convertedRequest = new CreateExpenseRequest(
                "Dinner",
                LocalDate.now(),
                "Team dinner",
                4500.0,
                "RUB"
        );


        doAnswer(invocation -> {
            CurrencyConverter.ConversionCallback callback = invocation.getArgument(3);
            callback.onSuccess(convertedRequest);
            return null;
        }).when(mockCurrencyConverter).convertToRub(eq(1L), any(CreateExpenseRequest.class), eq("valid_token"), any());

        String responseJson = "{" +
                "\"id\": 1," +
                "\"name\": \"Dinner\"," +
                "\"amount\": 4500.0," +
                "\"currency\": \"RUB\"" +
                "}";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(responseJson));

        MutableLiveData<Expense> liveData = new MutableLiveData<>();

        // Act
        expenseRepository.createExpenseWithConversion(1L, originalRequest, "valid_token", liveData);

        // Assert
        Expense result = getOrAwaitValue(liveData);
        assertNotNull(result);
        assertEquals(4500.0, result.getAmount(), 0.001);
        assertEquals("RUB", result.getCurrency());
    }

    // Тест для обработки нулевых значений в запросе

    @Test
    public void createExpense_shouldHandleNullValuesInRequest() throws Exception {
        // Arrange
        CreateExpenseRequest request = new CreateExpenseRequest(
                "Coffee",
                null,
                null,
                3.5,
                "USD"
        );

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        MutableLiveData<Expense> liveData = new MutableLiveData<>();

        // Act
        expenseRepository.createExpense(1L, request, "valid_token", liveData);

        // Assert
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String requestBody = recordedRequest.getBody().readUtf8();
        assertTrue(requestBody.contains("\"name\":\"Coffee\""));
        assertTrue(requestBody.contains("\"amount\":3.5"));
        assertTrue(requestBody.contains("\"currency\":\"USD\""));
        assertFalse(requestBody.contains("\"date\""));
        assertFalse(requestBody.contains("\"description\""));
    }

    // Тест для проверки формата даты

    @Test
    public void createExpense_shouldFormatDateCorrectly() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2023, 12, 31);
        CreateExpenseRequest request = new CreateExpenseRequest(
                "New Year Party",
                testDate,
                "Office celebration",
                200.0,
                "USD"
        );

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        MutableLiveData<Expense> liveData = new MutableLiveData<>();

        // Act
        expenseRepository.createExpense(1L, request, "valid_token", liveData);

        // Assert
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertTrue(recordedRequest.getBody().readUtf8().contains("\"date\":\"2023-12-31\""));
    }

    // Тест для проверки граничных значений суммы

    @Test
    public void createExpense_shouldHandleAmountBoundaries() throws Exception {
        // Arrange
        CreateExpenseRequest zeroAmount = new CreateExpenseRequest(
                "Free Event",
                null,
                null,
                0.0,
                "USD"
        );

        CreateExpenseRequest largeAmount = new CreateExpenseRequest(
                "Conference",
                null,
                null,
                999999.99,
                "USD"
        );

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        MutableLiveData<Expense> liveData1 = new MutableLiveData<>();
        MutableLiveData<Expense> liveData2 = new MutableLiveData<>();

        // Act
        expenseRepository.createExpense(1L, zeroAmount, "valid_token", liveData1);
        expenseRepository.createExpense(1L, largeAmount, "valid_token", liveData2);

        // Assert
        RecordedRequest request1 = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertJsonAmountEquals(request1, 0.0);

        RecordedRequest request2 = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertJsonAmountEquals(request2, 999999.99);
    }

    private void assertJsonAmountEquals(RecordedRequest request, double expectedAmount) throws Exception {
        if (request == null) {
            throw new AssertionError("Request was not received in time");
        }

        String body = request.getBody().readUtf8();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        double actualAmount = json.get("amount").getAsDouble();

        assertEquals("Amount value mismatch in JSON body", expectedAmount, actualAmount, 0.0001);
    }

    // Тест для проверки разных валют

    @Test
    public void createExpense_shouldSupportDifferentCurrencies() throws Exception {
        // Arrange
        CreateExpenseRequest[] requests = {
                new CreateExpenseRequest("Test 1", null, null, 10.0, "USD"),
                new CreateExpenseRequest("Test 2", null, null, 10.0, "EUR"),
                new CreateExpenseRequest("Test 3", null, null, 10.0, "GBP"),
                new CreateExpenseRequest("Test 4", null, null, 10.0, "JPY")
        };

        for (CreateExpenseRequest request : requests) {
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
            MutableLiveData<Expense> liveData = new MutableLiveData<>();

            // Act
            expenseRepository.createExpense(1L, request, "valid_token", liveData);

            // Assert
            RecordedRequest recordedRequest = mockWebServer.takeRequest();
            String requestBody = recordedRequest.getBody().readUtf8();
            assertTrue(requestBody.contains("\"currency\":\"" + request.getCurrency() + "\""));
        }
    }



}