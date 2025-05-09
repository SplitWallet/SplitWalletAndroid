package com.example.splitwallet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.splitwallet.databinding.ItemExpenseBinding;
import com.example.splitwallet.models.Expense;
import com.example.splitwallet.models.ExpenseAdapter;
import com.example.splitwallet.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class ExpenseAdapterTest {

    private Context context;
    private ExpenseAdapter adapter;

    @Mock
    private ExpenseAdapter.OnExpenseClickListener listener;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        adapter = new ExpenseAdapter();
    }

    @Test
    public void testOnBindViewHolder() {
        // Настройка
        Expense expense = new Expense();
        expense.setName("Test Expense");
        expense.setAmount(100.50);
        expense.setCurrency("USD");
        expense.setDate(LocalDate.of(2024, 5, 8));
        expense.setDescription("Test Description");
        expense.setUserWhoCreatedId("user1");

        User user = new User();
        user.setUsername("Test User");

        adapter.setMembersMap(Collections.singletonMap("user1", user));
        adapter.submitList(Collections.singletonList(expense));

        // Создание ViewHolder
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup parent = new FrameLayout(context);
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(inflater, parent, false);
        ExpenseAdapter.ExpenseViewHolder viewHolder = adapter.new ExpenseViewHolder(binding);

        adapter.onBindViewHolder(viewHolder, 0);

        // Проверки
        assertEquals("Test Expense", binding.tvName.getText().toString());

        // Проверка суммы (может быть "USD 100.50" или "USD 100,50" в зависимости от Locale)
        String amountText = binding.tvAmount.getText().toString();
        assertTrue(amountText.startsWith("USD"));
        assertTrue(amountText.contains("100"));

        assertEquals("Test Description", binding.tvDescription.getText().toString());
        assertEquals("Добавил: Test User", binding.tvCreatedBy.getText().toString());
    }

    @Test
    public void testOnExpenseClickListener() {
        adapter.setOnExpenseClickListener(listener);

        // Создаем реальный Expense
        Expense expense = new Expense();
        expense.setName("Click Test");
        expense.setAmount(77.77);
        expense.setCurrency("USD");
        expense.setDate(LocalDate.of(2024, 4, 4));
        expense.setDescription("For click");
        expense.setUserWhoCreatedId("user1");

        adapter.submitList(Collections.singletonList(expense));

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup parent = new FrameLayout(context);
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(inflater, parent, false);
        ExpenseAdapter.ExpenseViewHolder viewHolder = adapter.new ExpenseViewHolder(binding);

        adapter.onBindViewHolder(viewHolder, 0);

        viewHolder.itemView.performClick();

        // Проверяем вызов listener с точно этим объектом
        verify(listener).onExpenseClick(expense);
    }

}
