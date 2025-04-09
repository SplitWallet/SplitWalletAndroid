package com.example.splitwallet.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.splitwallet.MainActivity;
import com.example.splitwallet.R;
import com.example.splitwallet.viewmodels.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверка авторизации
        if (isUserRegistered()) {
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Закрыть LoginActivity
            return;
        }

        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        EditText etLogin = findViewById(R.id.etLogin);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String login = etLogin.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            viewModel.register(login, email, password);
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Закрыть RegisterActivity
        });

        viewModel.getTokenLiveData().observe(this, token -> {
            if (token != null) {
                // Сохраняем токен в SharedPreferences
                saveToken(String.valueOf(token));
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Закрыть LoginActivity
            } else {
                Toast.makeText(this, "Register failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private boolean isUserRegistered() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token != null; // Если токен есть, пользователь авторизован
    }
}