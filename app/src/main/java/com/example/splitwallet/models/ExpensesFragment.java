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
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.splitwallet.R;
import com.example.splitwallet.ui.LoginActivity;
import com.example.splitwallet.viewmodels.ExpenseViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpensesFragment extends Fragment {
    private Long groupId;
    private ExpenseViewModel expenseViewModel;
    private ExpenseAdapter adapter;
    private FloatingActionButton fabMain;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private ParticipantDistributionAdapter participantDistributionAdapter;
    private ParticipantDistributionAdapter distributionAdapter;

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
        // Только инфлейтим представление, не работаем с View здесь
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyView = view.findViewById(R.id.emptyView);

        // Инициализация всех View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView emptyView = view.findViewById(R.id.emptyView);
        fabMain = view.findViewById(R.id.fabMain);


        // Настройка RecyclerView
        adapter = new ExpenseAdapter();
        adapter.setOnExpenseClickListener(expense -> {
            showEditExpenseDialog(expense);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadExpenses();
        });

        // Загрузка данных
        //loadExpenses();

        // Наблюдатели LiveData
        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                adapter.submitList(expenses);
                emptyView.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        expenseViewModel.getGroupMembersMap().observe(getViewLifecycleOwner(), membersMap -> {
            adapter.setMembersMap(membersMap);
            adapter.notifyDataSetChanged();
        });

        String token = getAuthToken();
        if (token != null) {
            expenseViewModel.loadExpensesWithMembers(groupId, token);
        }

        expenseViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        // Настройка FAB
        setupFAB();
    }

    private void loadExpenses() {
        swipeRefreshLayout.setRefreshing(true);
        String token = getAuthToken();
        if (token != null) {
            expenseViewModel.loadExpenses(groupId, token);
        }

        expenseViewModel.getExpensesLiveData().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                adapter.submitList(expenses);
                emptyView.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        expenseViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                expenseViewModel.clearError();
            }
        });
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

    private void showEditExpenseDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Expense");

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_expense, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        Spinner spinnerCurrency = dialogView.findViewById(R.id.spinnerCurrency);
        Button btnMoreOptions = dialogView.findViewById(R.id.btnMoreOptions);

        // Заполняем поля данными расхода
        etName.setText(expense.getName());
        etDescription.setText(expense.getDescription());
        etAmount.setText(String.valueOf(expense.getAmount()));
        etDate.setText(expense.getDate().toString());

        // Настройка спиннера валют
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.currencies,
                android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        // Устанавливаем выбранную валюту
        int currencyPosition = currencyAdapter.getPosition(expense.getCurrency());
        if (currencyPosition >= 0) {
            spinnerCurrency.setSelection(currencyPosition);
        }

        // Настройка DatePicker
        etDate.setOnClickListener(v -> {
            LocalDate currentDate = LocalDate.parse(etDate.getText().toString());
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                        etDate.setText(selectedDate.toString());
                    },
                    currentDate.getYear(),
                    currentDate.getMonthValue() - 1,
                    currentDate.getDayOfMonth());
            datePicker.show();
        });
        btnMoreOptions.setOnClickListener(v -> {
            showExpenseSettingsDialog(groupId, expense.getId(), expense.getAmount());
        });
        builder.setPositiveButton("Save", (dialog, which) -> {
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

                // Получаем текущее распределение
                List<ExpenseUser> participants = getUpdatedDistributionFromAdapter();
                double sumParticipants = participants.stream()
                        .mapToDouble(ExpenseUser::getAmount)
                        .sum();

                // Проверяем условие
                if (amount < sumParticipants) {
                    Toast.makeText(requireContext(),
                            "Сумма расходов (" + amount + ") меньше суммы распределений (" + sumParticipants + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Если всё ок, отправляем
                UpdateExpenseRequest request = new UpdateExpenseRequest(
                        name, date, description, amount, currency);

                String token = getAuthToken();
                if (token != null) {
                    expenseViewModel.updateExpense(groupId, expense.getId(), request, token, participants);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            } catch (DateTimeParseException e) {
                Toast.makeText(requireContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        // Добавляем кнопку удаления
        builder.setNeutralButton("Delete", (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Expense")
                    .setMessage("Are you sure you want to delete this expense?")
                    .setPositiveButton("Delete", (d, w) -> {
                        String token = getAuthToken();
                        if (token != null) {
                            expenseViewModel.deleteExpense(
                                    groupId, expense.getId(), token);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        builder.create().show();
    }

    private void setupFAB() {
        fabMain.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), fabMain);
            popupMenu.getMenuInflater().inflate(R.menu.expense_add_options, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_add_manual) {
                    showAddExpenseDialog();
                    return true;
                } else if (item.getItemId() == R.id.menu_add_qr) {
                    startQRScanner();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }

    private void startQRScanner() {
        try {
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan receipt QR code");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.initiateScan();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "QR scanner not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Обработка результата сканирования
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                parseQRData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void parseQRData(String qrData) {
        try {
            // Пример QR: t=20250405T1853&s=19837.00&fn=7381440700168628&i=10975&fp=3291082750&n=1
            Map<String, String> params = Arrays.stream(qrData.split("&"))
                    .map(pair -> pair.split("="))
                    .filter(keyValue -> keyValue.length == 2)
                    .collect(Collectors.toMap(
                            keyValue -> keyValue[0],
                            keyValue -> keyValue[1]
                    ));

            // Парсим дату из формата yyyyMMdd (первые 8 символов до 'T')
            String dateStr = params.get("t").substring(0, 8);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(dateStr, formatter);

            // Создаем расход на основе данных QR
            CreateExpenseRequest request = new CreateExpenseRequest(
                    "Чек " + params.get("fn") + "-" + params.get("i"), // Название
                    date, // Дата
                    "Оплата по чеку", // Описание
                    Double.parseDouble(params.get("s")), // Сумма
                    "RUB" // Валюта
            );

            String token = getAuthToken();
            if (token != null) {
                expenseViewModel.createExpense(groupId, request, token);
                Toast.makeText(requireContext(), "Расход добавлен", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ошибка обработки QR: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("QR_ERROR", "Error parsing QR: " + qrData, e);
        }
    }

    private Map<String, String> parseQRParameters(String qrData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = qrData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private void showExpenseSettingsDialog(Long groupId, Long expenseId, Double totalAmount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_expense_settings, null);
        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.participantsRecyclerView);
        Button btnSave = dialogView.findViewById(R.id.btnSaveDistribution);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        AlertDialog dialog = builder.create();

        // Загружаем данные
        expenseViewModel.loadExpenseUsers(groupId, expenseId, getAuthToken());

        // Объединяем observer'ы, чтобы избежать вложенности
        expenseViewModel.getGroupMembersMap().observe(getViewLifecycleOwner(), membersMap -> {
            List<ExpenseUser> expenseUsers = expenseViewModel.getExpenseUsersLiveData().getValue();
            updateAdapter(membersMap, expenseUsers, totalAmount, recyclerView);
        });

        expenseViewModel.getExpenseUsersLiveData().observe(getViewLifecycleOwner(), expenseUsers -> {
            Map<String, User> membersMap = expenseViewModel.getGroupMembersMap().getValue();
            updateAdapter(membersMap, expenseUsers, totalAmount, recyclerView);
        });

        btnSave.setOnClickListener(v -> {
            if (distributionAdapter == null) {
                Toast.makeText(getContext(), "Данные еще не загружены", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ExpenseUser> updatedDistribution = distributionAdapter.getUpdatedDistribution();

            if (updatedDistribution.isEmpty()) {
                Toast.makeText(getContext(), "Выберите хотя бы одного участника", Toast.LENGTH_SHORT).show();
                return;
            }

            double sum = updatedDistribution.stream()
                    .mapToDouble(ExpenseUser::getAmount)
                    .sum();

            if (Math.abs(sum - totalAmount) > 0.01) {
                Toast.makeText(getContext(),
                        String.format("Сумма должна равняться %.2f", totalAmount),
                        Toast.LENGTH_SHORT).show();
            } else {
                expenseViewModel.updateExpenseUsers(groupId, expenseId, getAuthToken(), updatedDistribution);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateAdapter(Map<String, User> membersMap, List<ExpenseUser> expenseUsers,
                               double totalAmount, RecyclerView recyclerView) {
        if (membersMap == null || expenseUsers == null) return;

        List<User> membersList = new ArrayList<>(membersMap.values());
        distributionAdapter = new ParticipantDistributionAdapter(
                membersList,
                expenseUsers,
                totalAmount,
                recyclerView
        );
        recyclerView.setAdapter(distributionAdapter);
    }

    private List<ExpenseUser> getUpdatedDistributionFromAdapter() {
        if (participantDistributionAdapter != null) {
            return participantDistributionAdapter.getUpdatedDistribution();
        }
        return new ArrayList<>();
    }

    private void saveDistribution(Long groupId, Long expenseId) {
        // Получаем данные из адаптера и отправляем на сервер
        List<ExpenseUser> updatedDistribution = getUpdatedDistributionFromAdapter();
        expenseViewModel.updateExpenseUsers(groupId, expenseId, getAuthToken(), updatedDistribution);
    }
}