package com.example.splitwallet.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.splitwallet.R;
import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.utils.InviteCodeUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinGroupActivity extends AppCompatActivity {

    private EditText codeInput;
    private Button joinButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        codeInput = findViewById(R.id.code_input);
        joinButton = findViewById(R.id.join_button);

        String rawToken = getIntent().getStringExtra("TOKEN");
        if (rawToken == null || rawToken.isEmpty()) {
            Toast.makeText(this, "Ошибка: токен не получен", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String token = "Bearer " + rawToken;
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        joinButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString();
            if (code.length() != 6 || TextUtils.isDigitsOnly(code) || code.equals("000000")) {
                Toast.makeText(this, "Попробуйте ввести код ещё раз", Toast.LENGTH_SHORT).show();
                return;
            }
            joinGroupByCode(code, token);
        });

    }

//    private String generateJoinCode(String groupId) {
//        int hash = groupId.hashCode();
//        int code = Math.abs(hash) % 1000000;
//        return String.format("%06d", code);
//    }

    private void joinGroupByCode(String code, String token) {
        Long groupId;
        try {
            groupId = InviteCodeUtil.decode(code); // декодер
        } catch (Exception e) {
            Toast.makeText(this, "Неверный формат кода", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.joinGroup(groupId, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(JoinGroupActivity.this, "Вы присоединились к группе!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish(); // закрываем экран
                } else {
                    Toast.makeText(JoinGroupActivity.this, "Группа не найдена или уже присоединены", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(JoinGroupActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
