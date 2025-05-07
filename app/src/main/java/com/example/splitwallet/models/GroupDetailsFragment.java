package com.example.splitwallet.models;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.MainActivity;
import com.example.splitwallet.R;
import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.ui.LoginActivity;
import com.example.splitwallet.ui.MemberDetailsActivity;
import com.example.splitwallet.ui.MembersAdapter;
import com.example.splitwallet.viewmodels.GroupViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupDetailsFragment extends Fragment {
    private Long groupId;
    private String groupName;
    private GroupViewModel groupViewModel;
    private MembersAdapter adapter;
    private Button btnSettleUp;

    public static GroupDetailsFragment newInstance(Long groupId, String groupName) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("groupId", groupId);
        args.putString("groupName", groupName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            groupId = getArguments().getLong("groupId");
            groupName = getArguments().getString("groupName");
        }
        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);

        RecyclerView membersRecyclerView = view.findViewById(R.id.membersRecyclerView);
        Button btnInvite = view.findViewById(R.id.btnInvite);
        Button btnLeaveGroup = view.findViewById(R.id.btnLeaveGroup);
        btnSettleUp = view.findViewById(R.id.btnSettleUp);
        btnSettleUp.setOnClickListener(v -> showSettleUpDialog());

        adapter = new MembersAdapter();
        adapter.setListener(this::onMemberClick);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        membersRecyclerView.setAdapter(adapter);

        loadGroupMembers();

        btnInvite.setOnClickListener(v -> showInviteDialog());
        btnLeaveGroup.setOnClickListener(v -> showLeaveGroupDialog());

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Получение токена не удалось", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "FCM токен: " + token);
                    sendTokenToServer(getAuthToken(),token, getCurrentUserId());
                });


        //  Подписка на выход из группы
        groupViewModel.getLeftGroupLiveData().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                requireActivity().onBackPressed();
            } else {
                int code = groupViewModel.getLastLeaveGroupResponseCode();
                String message;

                switch (code) {
                    case 400:
                        message = "Владелец группы не может её покинуть";
                        break;
                    case 500:
                        message = "Внутренняя ошибка сервера";
                        break;
                    default:
                        message = "Ошибка выхода из группы";
                        break;
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadGroupMembers() {
        String token = getAuthToken();
        if (token != null) {
            groupViewModel.loadGroupMembers(groupId, token);
            groupViewModel.getGroupMembersLiveData().observe(getViewLifecycleOwner(), members -> {
                if (members != null) {
                    adapter.updateMembers(members);
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки участников", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(getContext(), "Ошибка аутентификации", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        }
        return token;
    }

    private void onMemberClick(UserResponse member) {
        Intent intent = new Intent(getActivity(), MemberDetailsActivity.class);
        intent.putExtra("memberName", member.getName());
        intent.putExtra("memberEmail", member.getEmail());
        intent.putExtra("debt", 0); // Заглушка для баланса
        startActivity(intent);
    }

    private void showInviteDialog() {
        String token = getAuthToken();
        if (token != null) {
            groupViewModel.fetchGroupInviteCode(groupId, token);
        }

        groupViewModel.getInviteCodeLiveData().observe(getViewLifecycleOwner(), inviteCode -> {
            if (inviteCode != null) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Код приглашения")
                        .setMessage("Поделитесь этим кодом: " + inviteCode)
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Toast.makeText(getContext(), "Не удалось получить код приглашения", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showLeaveGroupDialog() {
        // Реализовано полноценное подтверждение выхода
        new AlertDialog.Builder(requireContext())
                .setTitle("Покинуть группу")
                .setMessage("Вы уверены, что хотите покинуть эту группу?")
                .setPositiveButton("Покинуть", (dialog, which) -> {
                    String token = getAuthToken();
                    String userId = getCurrentUserId();
                    if (token != null && userId != null && !userId.isEmpty()) {
                        groupViewModel.leaveGroup(groupId, userId, token);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private AlertDialog settleUpDialog;
    private void showSettleUpDialog() {
        String token = getAuthToken();
        if (token == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_settle_up, null);
        builder.setView(dialogView);
        builder.setTitle("Settle Up - " + groupName);

        Button btnClose = dialogView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            if (settleUpDialog != null && settleUpDialog.isShowing()) {
                settleUpDialog.dismiss();
            }
        });

        RecyclerView recyclerView = dialogView.findViewById(R.id.balancesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        settleUpDialog = builder.create();

        Observer<List<Balance>> balancesObserver = new Observer<List<Balance>>() {
            @Override
            public void onChanged(List<Balance> balances) {
                if (balances != null) {
                    BalanceAdapter adapter = new BalanceAdapter(balances);
                    recyclerView.setAdapter(adapter);
                } else {
                    if (settleUpDialog != null && settleUpDialog.isShowing()) {
                        settleUpDialog.dismiss();
                    }
                    Toast.makeText(getContext(), "Failed to load balances", Toast.LENGTH_SHORT).show();
                }
            }
        };

        groupViewModel.getGroupBalancesLiveData().observe(getViewLifecycleOwner(), balancesObserver);

        settleUpDialog.setOnDismissListener(dialog -> {
            groupViewModel.getGroupBalancesLiveData().removeObserver(balancesObserver);
        });

        settleUpDialog.show();

        groupViewModel.loadGroupBalances(groupId, token);
    }

    @Override
    public void onDestroyView() {
        if (settleUpDialog != null && settleUpDialog.isShowing()) {
            settleUpDialog.dismiss();
            settleUpDialog = null;
        }
        super.onDestroyView();
    }

    private String getCurrentUserId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return extractUserIdFromJwt(token);
    }

    private String extractUserIdFromJwt(String jwtToken) {
        try {
            if (jwtToken == null) return null;

            String[] parts = jwtToken.split("\\.");
            if (parts.length >= 2) {
                String payloadJson = new String(
                        android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
                org.json.JSONObject payload = new org.json.JSONObject(payloadJson);
                return payload.getString("sub");
            }
        } catch (Exception e) {
            Log.e("JWT_ERROR", "Ошибка извлечения userId из токена", e);
        }
        return null;
    }

    private void sendTokenToServer(String authToken, String fcmToken, String userId){
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        TokenRequest tokenRequest = new TokenRequest(fcmToken);

        Call<Void> call = apiService.updateFcmToken("Bearer " + authToken, userId, tokenRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    Log.e("FCM", "Failed to send token to server. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Failed to send token to server", t);
            }
        });

    }
}
