package com.example.splitwallet;

import static org.mockito.Mockito.*;

import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.repository.ExpenseRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.example.splitwallet.repository.ExpenseRepository.ExpensesCallback;

import org.junit.Rule;
import org.mockito.Captor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ExpenseRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiService apiService;

    @Mock
    private CurrencyConverter currencyConverter;

    @Mock
    private Call<List<Expense>> expenseListCall;

    @Mock
    private Call<Expense> expenseCall;

    @Captor
    private ArgumentCaptor<Callback<List<Expense>>> expensesCallbackCaptor;

    @Captor
    private ArgumentCaptor<Callback<Expense>> expenseCallbackCaptor;

    private ExpenseRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new ExpenseRepository(apiService, currencyConverter);
    }

//    @Test
//    public void testGetExpenses_success() {
//        Long groupId = 1L;
//        String token = "test-token";
//        ExpensesCallback callback = mock(ExpensesCallback.class);
//
//        when(apiService.getGroupExpenses(eq(groupId), anyString())).thenReturn(expenseListCall);
//
//        repository.getExpenses(groupId, token, callback);
//        verify(expenseListCall).enqueue(expensesCallbackCaptor.capture());
//
//        List<Expense> mockExpenses = Arrays.asList(new Expense(), new Expense());
//        Response<List<Expense>> response = Response.success(mockExpenses);
//
//        expensesCallbackCaptor.getValue().onResponse(expenseListCall, response);
//
//        verify(callback).onSuccess(mockExpenses);
//    }

    @Test
    public void testGetExpenses_failure() {
        Long groupId = 1L;
        String token = "test-token";
        ExpensesCallback callback = mock(ExpensesCallback.class);

        when(apiService.getGroupExpenses(eq(groupId), anyString())).thenReturn(expenseListCall);

        repository.getExpenses(groupId, token, callback);
        verify(expenseListCall).enqueue(expensesCallbackCaptor.capture());

        Throwable t = new Throwable("Network error");
        expensesCallbackCaptor.getValue().onFailure(expenseListCall, t);

        verify(callback).onError("Network error: Network error");
    }

    @Test
    public void testCreateExpense_rubCurrency() {
        Long groupId = 1L;
        String token = "test-token";
        MutableLiveData<Expense> result = new MutableLiveData<>();
        CreateExpenseRequest request = new CreateExpenseRequest(
                "Lunch", LocalDate.now(), "Business lunch", 100.0, "RUB"
        );

        when(apiService.createExpense(eq(groupId), anyString(), any())).thenReturn(expenseCall);

        repository.createExpenseWithConversion(groupId, request, token, result);
        verify(apiService).createExpense(eq(groupId), eq("Bearer " + token), eq(request));
    }

    @Test
    public void testCreateExpense_nonRubCurrency_successfulConversion() {
        Long groupId = 1L;
        String token = "test-token";
        MutableLiveData<Expense> result = new MutableLiveData<>();
        CreateExpenseRequest request = new CreateExpenseRequest(
                "Dinner", LocalDate.now(), "Team dinner", 100.0, "USD"
        );

        doAnswer(invocation -> {
            CurrencyConverter.ConversionCallback callback = invocation.getArgument(3);
            CreateExpenseRequest convertedRequest = new CreateExpenseRequest(
                    "Dinner", request.getDate(), request.getDescription(), 9000.0, "RUB"
            );
            callback.onSuccess(convertedRequest);
            return null;
        }).when(currencyConverter).convertToRub(eq(groupId), eq(request), eq(token), any());

        when(apiService.createExpense(eq(groupId), anyString(), any())).thenReturn(expenseCall);

        repository.createExpenseWithConversion(groupId, request, token, result);
        verify(apiService).createExpense(eq(groupId), eq("Bearer " + token), any());
    }
}


