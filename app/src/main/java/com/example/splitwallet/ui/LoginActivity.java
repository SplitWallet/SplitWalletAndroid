package com.example.splitwallet.ui;

import static android.content.ContentValues.TAG;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.splitwallet.MainActivity;
import com.example.splitwallet.R;
import com.example.splitwallet.auth.GoogleAuthHelper;
import com.example.splitwallet.viewmodels.LoginViewModel;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private Button btnGoogleSignIn;

    private BeginSignInRequest signInRequest;

    private static final int REQ_ONE_TAP = 100;
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient googleSignInClient;


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

        // Инициализация Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id),false)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Обработчик кнопки Google Sign-In
        findViewById(R.id.btnGoogleSignIn).setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



    private void verifyGoogleTokenOnServer(String idToken) {
        // Здесь нужно отправить токен на ваш сервер для верификации
        // и получения JWT токена вашего приложения

        // Примерный код (нужно адаптировать под ваш API):
        viewModel.loginWithGoogle(idToken);
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
    public void buttonGoogleSignIn(View view){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private class RetrieveAccessTokenTask extends AsyncTask<GoogleSignInAccount, Void, String> {
        private GoogleSignInAccount account;

        @Override
        protected String doInBackground(GoogleSignInAccount... accounts) {
            this.account = accounts[0];
            try {
                String scope = "oauth2:profile email"; // или "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"
                Account acct = new Account(account.getEmail(), "com.google");
                return GoogleAuthUtil.getToken(getApplicationContext(), acct, scope);
            } catch (IOException | GoogleAuthException e) {
                Log.e(TAG, "Failed to get access token", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String accessToken) {
            if (accessToken != null) {
                Log.d(TAG, "Access Token: " + accessToken);
                // Теперь у вас есть access token (ya29.a0AZYkNZh...)
                // Отправляем его на сервер
                verifyGoogleTokenOnServer(accessToken);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to get access token", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                // Получаем access token асинхронно
                new RetrieveAccessTokenTask().execute(account);
            }
        } catch (ApiException e) {
            Log.e("GoogleSignIn", "Error code: " + e.getStatusCode());
            Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void signOutFromGoogle(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Google sign-out successful");
            } else {
                Log.e(TAG, "Google sign-out failed", task.getException());
            }
        });
    }

}