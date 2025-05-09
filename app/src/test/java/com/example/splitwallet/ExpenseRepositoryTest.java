package com.example.splitwallet;

import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.repository.ExpenseRepository;
import com.example.splitwallet.repository.ExpenseRepository.ExpensesCallback;
import com.example.splitwallet.repository.ExpenseRepository.ExpenseCallback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ExpenseRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiService apiService;

    @Mock
    private CurrencyConverter currencyConverter;

    private ExpenseRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        repository = new ExpenseRepository(apiService, currencyConverter);
    }

    @Test
    public void testGetExpenses_callsApi() {
        Long groupId = 1L;
        String token = "testToken";
        ExpensesCallback callback = mock(ExpensesCallback.class);
        Call<List<Expense>> call = mock(Call.class);

        when(apiService.getGroupExpenses(groupId, "Bearer " + token)).thenReturn(call);

        repository.getExpenses(groupId, token, callback);

        verify(call).enqueue(any());
    }

    @Test
    public void testCreateExpense_callsApi() {
        Long groupId = 1L;
        String token = "testToken";
        CreateExpenseRequest request = new CreateExpenseRequest("Test", LocalDate.now(), "desc", 100.0, "RUB");
        MutableLiveData<Expense> result = new MutableLiveData<>();
        Call<Expense> call = mock(Call.class);

        when(apiService.createExpense(groupId, "Bearer " + token, request)).thenReturn(call);

        repository.createExpense(groupId, request, token, result);

        verify(call).enqueue(any());
    }

    @Test
    public void testCreateExpenseWithConversion_ifCurrencyIsRub_callsCreateExpenseDirectly() {
        Long groupId = 1L;
        String token = "testToken";
        CreateExpenseRequest request = new CreateExpenseRequest("Test", LocalDate.now(), "desc", 100.0, "RUB");
        MutableLiveData<Expense> result = new MutableLiveData<>();
        ExpenseRepository spyRepo = spy(repository);

        doNothing().when(spyRepo).createExpense(groupId, request, token, result);

        spyRepo.createExpenseWithConversion(groupId, request, token, result);

        verify(spyRepo).createExpense(groupId, request, token, result);
    }

    @Test
    public void testCreateExpenseWithConversion_ifCurrencyIsUSD_callsConverter() {
        Long groupId = 1L;
        String token = "testToken";
        CreateExpenseRequest request = new CreateExpenseRequest("Test", LocalDate.now(), "desc", 50.0, "USD");
        MutableLiveData<Expense> result = new MutableLiveData<>();

        repository.createExpenseWithConversion(groupId, request, token, result);

        verify(currencyConverter).convertToRub(eq(groupId), eq(request), eq(token), any());
    }

    @Test
    public void testUpdateExpenseUsers_callsApi() {
        Long groupId = 1L;
        Long expenseId = 2L;
        String token = "testToken";
        Callback<List<ExpenseUser>> callback = mock(Callback.class);
        Call<List<ExpenseUser>> call = mock(Call.class);

        List<ExpenseUser> expenseUsers = List.of(new ExpenseUser("user1", 100.0, 50.0));
        when(apiService.updateExpenseUsers(groupId, expenseId, "Bearer " + token, expenseUsers)).thenReturn(call);

        repository.updateExpenseUsers(groupId, expenseId, token, expenseUsers, callback);

        verify(call).enqueue(eq(callback));
    }

    @Test
    public void testDeleteExpense_callsApi() {
        Long groupId = 1L;
        Long expenseId = 10L;
        String token = "testToken";
        Call<Void> call = mock(Call.class);
        ExpenseCallback callback = mock(ExpenseCallback.class);

        when(apiService.deleteExpense(groupId, expenseId, "Bearer " + token)).thenReturn(call);

        repository.deleteExpense(groupId, expenseId, token, callback);

        verify(call).enqueue(any());
    }

    @Test
    public void testGetExpenseUsers_callsApi() {
        Long groupId = 1L;
        Long expenseId = 2L;
        String token = "testToken";
        Callback<List<ExpenseUser>> callback = mock(Callback.class);
        Call<List<ExpenseUser>> call = mock(Call.class);

        when(apiService.getExpenseUsers(groupId, expenseId, "Bearer " + token)).thenReturn(call);

        repository.getExpenseUsers(groupId, expenseId, token, callback);

        verify(call).enqueue(eq(callback));
    }

    @Test
    public void testRemoveUserFromExpense_callsApi() {
        Long groupId = 1L;
        Long expenseId = 2L;
        String userId = "user1";
        String token = "testToken";
        Callback<Void> callback = mock(Callback.class);
        Call<Void> call = mock(Call.class);

        when(apiService.removeUserFromExpense(groupId, expenseId, userId, "Bearer " + token)).thenReturn(call);

        repository.removeUserFromExpense(groupId, expenseId, userId, token, callback);

        verify(call).enqueue(eq(callback));
    }
}
