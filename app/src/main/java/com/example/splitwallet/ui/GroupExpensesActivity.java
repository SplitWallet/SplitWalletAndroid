package com.example.splitwallet.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.splitwallet.R;
import com.example.splitwallet.databinding.ActivityGroupExpensesBinding;
import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.ExpenseAdapter;
import com.example.splitwallet.viewmodels.ExpenseViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class GroupExpensesActivity extends AppCompatActivity {
    private ActivityGroupExpensesBinding binding;
    private ExpenseViewModel expenseViewModel;
    private Long groupId;
    private String token;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Получаем groupId из интента
        groupId = getIntent().getLongExtra("groupId", -1);
        if (groupId == -1) {
            finish();
            return;
        }


        // Настройка Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Group Expenses");
        }

        // Обработка кнопки "Назад"
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding = ActivityGroupExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Инициализация ViewModel
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Настройка RecyclerView
        adapter = new ExpenseAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Загрузка расходов
        expenseViewModel.loadExpenses(groupId, getAuthToken());

        // Наблюдаем за изменениями списка расходов
        expenseViewModel.getExpensesLiveData().observe(this, expenses -> {
            if (expenses != null) {
                adapter.submitList(expenses);
                binding.emptyView.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(this, "Failed to load expenses", Toast.LENGTH_SHORT).show();
            }
        });

        // Наблюдаем за новыми расходами
        expenseViewModel.getNewExpenseLiveData().observe(this, expense -> {
            if (expense != null) {
                expenseViewModel.loadExpenses(groupId, getAuthToken()); // Обновляем список
                Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Ошибка доступа. Проверьте авторизацию", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка добавления расхода
        binding.fab.setOnClickListener(view -> showAddExpenseDialog());
    }

    private void showAddExpenseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Expense");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_expense, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        Spinner spinnerCurrency = dialogView.findViewById(R.id.spinnerCurrency);

        // Установка текущей даты по умолчанию
        etDate.setText(LocalDate.now().toString());

        // Настройка DatePicker
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                        etDate.setText(selectedDate.toString());
                    },
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue() - 1,
                    LocalDate.now().getDayOfMonth());
            datePicker.show();
        });

        // Настройка спиннера валют
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString();
            String description = etDescription.getText().toString();
            String amountStr = etAmount.getText().toString();
            String dateStr = etDate.getText().toString();
            String currency = spinnerCurrency.getSelectedItem().toString();

            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Name and amount are required", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                LocalDate date = LocalDate.parse(dateStr);

                CreateExpenseRequest request = new CreateExpenseRequest(
                        name, date, description, amount, currency);
                Log.d("EXPENSE_DEBUG", "Token: " + getAuthToken()); // Для отладки
                //expenseViewModel.createExpense(groupId, request, getAuthToken());
                expenseViewModel.createExpenseWithConversion(groupId, request, getAuthToken());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            } catch (DateTimeParseException e) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Ошибка: токен не найден. Пожалуйста, войдите снова.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return null;
        }
        return token;
    }
}