package com.example.splitwallet.models;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;
import com.example.splitwallet.ui.LoginActivity;
import com.example.splitwallet.viewmodels.ExpenseViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ExpensesFragment extends Fragment {
    private Long groupId;
    private ExpenseViewModel expenseViewModel;
    private ExpenseAdapter adapter;

    public static ExpensesFragment newInstance(Long groupId) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putLong("groupId", groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getLong("groupId");
        }
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView emptyView = view.findViewById(R.id.emptyView);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        adapter = new ExpenseAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadExpenses();

        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                adapter.submitList(expenses);
                emptyView.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
            }
        });

        expenseViewModel.getNewExpenseLiveData().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                loadExpenses(); // Обновляем список
                Toast.makeText(getContext(), "Expense added", Toast.LENGTH_SHORT).show();
            }
        });

        fab.setOnClickListener(v -> showAddExpenseDialog());

        return view;
    }

    private void loadExpenses() {
        String token = getAuthToken();
        if (token != null) {
            expenseViewModel.loadExpenses(groupId, token);
        }
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(getContext(), "Authentication error", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        }
        return token;
    }

    private void showAddExpenseDialog() {
        // Используем requireContext() вместо this
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Expense");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_expense, null);
        builder.setView(dialogView);

        // Остальной код метода остается без изменений
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        Spinner spinnerCurrency = dialogView.findViewById(R.id.spinnerCurrency);

        etDate.setText(LocalDate.now().toString());

        etDate.setOnClickListener(v -> {
            LocalDate currentDate = LocalDate.now();
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(), // Используем requireContext() здесь тоже
                    (view, year, month, dayOfMonth) -> {
                        LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                        etDate.setText(selectedDate.toString());
                    },
                    currentDate.getYear(),
                    currentDate.getMonthValue() - 1,
                    currentDate.getDayOfMonth());
            datePicker.show();
        });

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                requireContext(), // И здесь
                R.array.currencies,
                android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString();
            String description = etDescription.getText().toString();
            String amountStr = etAmount.getText().toString();
            String dateStr = etDate.getText().toString();
            String currency = spinnerCurrency.getSelectedItem().toString();

            if (name.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Name and amount are required", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            } catch (DateTimeParseException e) {
                Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}