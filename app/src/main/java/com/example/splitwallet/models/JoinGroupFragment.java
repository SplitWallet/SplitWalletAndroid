package com.example.splitwallet.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.splitwallet.R;
import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.utils.InviteCodeUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinGroupFragment extends Fragment {

    private EditText codeInput;
    private Button joinButton;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        codeInput = view.findViewById(R.id.code_input);
        joinButton = view.findViewById(R.id.join_button);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String rawToken = sharedPreferences.getString("token", null);

        if (rawToken == null || rawToken.isEmpty()) {
            Toast.makeText(requireContext(), "Ошибка: токен не получен", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = "Bearer " + rawToken;
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        joinButton.setOnClickListener(v -> {
            String code = codeInput.getText().toString();
            if (code.length() != 6 || TextUtils.isDigitsOnly(code) || code.equals("000000")) {
                Toast.makeText(requireContext(), "Попробуйте ввести код ещё раз", Toast.LENGTH_SHORT).show();
                return;
            }
            joinGroupByCode(code, token);
        });
    }

    private void joinGroupByCode(String code, String token) {
        Long groupId;
        try {
            groupId = InviteCodeUtil.decode(code);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Неверный формат кода", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.joinGroup(groupId, token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Вы присоединились к группе!", Toast.LENGTH_SHORT).show();
                    // можно дополнительно обновить группы из ViewModel, если хочешь
                } else {
                    Toast.makeText(requireContext(), "Группа не найдена или уже присоединены", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
