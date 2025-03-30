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
import com.example.splitwallet.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверка авторизации
        if (isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Закрыть LoginActivity
            return;
        }

        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        EditText etLogin = findViewById(R.id.etLogin);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString();
            String password = etPassword.getText().toString();
            viewModel.login(login, password);
        });

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        viewModel.getTokenLiveData().observe(this, token -> {
            if (token != null) {
                // Сохраняем токен в SharedPreferences
                saveToken(token.getJwtToken());
                startActivity(new Intent(this, MainActivity.class));
                finish(); // Закрыть LoginActivity
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token != null; // Если токен есть, пользователь авторизован
    }
}