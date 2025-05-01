//package com.example.splitwallet.models;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.splitwallet.R;
//import com.example.splitwallet.ui.LoginActivity;
//import com.example.splitwallet.ui.MemberDetailsActivity;
//import com.example.splitwallet.ui.MembersAdapter;
//import com.example.splitwallet.utils.InviteCodeUtil;
//import com.example.splitwallet.viewmodels.GroupViewModel;
//
//import java.util.Objects;
//
//public class GroupDetailsFragment extends Fragment {
//    private Long groupId;
//    private String groupName;
//    private GroupViewModel groupViewModel;
//    private MembersAdapter adapter;
//
//    public static GroupDetailsFragment newInstance(Long groupId, String groupName) {
//        GroupDetailsFragment fragment = new GroupDetailsFragment();
//        Bundle args = new Bundle();
//        args.putLong("groupId", groupId);
//        args.putString("groupName", groupName);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            groupId = getArguments().getLong("groupId");
//            groupName = getArguments().getString("groupName");
//        }
//        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_group_details, container, false);
//
//        RecyclerView membersRecyclerView = view.findViewById(R.id.membersRecyclerView);
//        Button btnInvite = view.findViewById(R.id.btnInvite);
//        Button btnLeaveGroup = view.findViewById(R.id.btnLeaveGroup);
//
//        adapter = new MembersAdapter();
//        adapter.setListener(this::onMemberClick);
//        membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        membersRecyclerView.setAdapter(adapter);
//
//        loadGroupMembers();
//
//        btnInvite.setOnClickListener(v -> showInviteDialog());
//        btnLeaveGroup.setOnClickListener(v -> showLeaveGroupDialog());
//
//        groupViewModel.getLeftGroupLiveData().observe(getViewLifecycleOwner(), success -> {
//            if (success != null && success) {
//                requireActivity().onBackPressed();
//            } else {
//                int code = groupViewModel.getLastLeaveGroupResponseCode();
//                String message;
//
//                switch (code) {
//                    case 400:
//                        message = "Владелец группы не может её покинуть";
//                        break;
//                    case 500:
//                        message = "Внутренняя ошибка сервера";
//                        break;
//                    default:
//                        message = "Ошибка выхода из группы";
//                        break;
//                }
//                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return view;
//    }
//
//    private void loadGroupMembers() {
//        String token = getAuthToken();
//        if (token != null) {
//            groupViewModel.loadGroupMembers(groupId, token);
//            groupViewModel.getGroupMembersLiveData().observe(getViewLifecycleOwner(), members -> {
//                if (members != null) {
//                    adapter.updateMembers(members);
//                } else {
//                    Toast.makeText(getContext(), "Ошибка загрузки участников", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//
//    private String getAuthToken() {
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("auth", MODE_PRIVATE);
//        String token = sharedPreferences.getString("token", null);
//        if (token == null) {
//            Toast.makeText(getContext(), "Authentication error", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//            requireActivity().finish();
//        }
//        return token;
//    }
//
//    private void onMemberClick(UserResponse member) {
//        Intent intent = new Intent(getActivity(), MemberDetailsActivity.class);
//        intent.putExtra("memberName", member.getName());
//        intent.putExtra("memberEmail", member.getEmail());
//        intent.putExtra("debt", 0); // Заглушка для баланса
//        startActivity(intent);
//    }
//
//    private void showInviteDialog() {
//        new AlertDialog.Builder(requireContext())
//                .setTitle("Invite Code")
//                .setMessage("Share this code: " + InviteCodeUtil.encode(groupId))
//                .setPositiveButton("OK", null)
//                .show();
//    }
//
//    private void showLeaveGroupDialog() {
//        new AlertDialog.Builder(requireContext())
//                .setTitle("Leave Group")
//                .setMessage("Are you sure you want to leave this group?")
//                .setPositiveButton("Leave", (dialog, which) -> {
//                    String token = getAuthToken();
//                    String userId = getCurrentUserId();
//                    if (token != null && userId != null && !userId.isEmpty()) {
//                        groupViewModel.leaveGroup(groupId, userId, token);
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }
//
//    private String getCurrentUserId() {
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("auth", MODE_PRIVATE);
//        String token = sharedPreferences.getString("token", null);
//        return extractUserIdFromJwt(token);
//    }
//
//    private String extractUserIdFromJwt(String jwtToken) {
//        try {
//            if (jwtToken == null) return null;
//
//            String[] parts = jwtToken.split("\\.");
//            if (parts.length >= 2) {
//                String payloadJson = new String(
//                        android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE));
//                org.json.JSONObject payload = new org.json.JSONObject(payloadJson);
//                return payload.getString("sub");
//            }
//        } catch (Exception e) {
//            Log.e("JWT_ERROR", "Error extracting user ID from token", e);
//        }
//        return null;
//    }
//}

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;
import com.example.splitwallet.ui.LoginActivity;
import com.example.splitwallet.ui.MemberDetailsActivity;
import com.example.splitwallet.ui.MembersAdapter;
import com.example.splitwallet.utils.InviteCodeUtil;
import com.example.splitwallet.viewmodels.GroupViewModel;

import java.util.Objects;

public class GroupDetailsFragment extends Fragment {
    private Long groupId;
    private String groupName;
    private GroupViewModel groupViewModel;
    private MembersAdapter adapter;

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

        adapter = new MembersAdapter();
        adapter.setListener(this::onMemberClick);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        membersRecyclerView.setAdapter(adapter);

        loadGroupMembers();

        btnInvite.setOnClickListener(v -> showInviteDialog());
        btnLeaveGroup.setOnClickListener(v -> showLeaveGroupDialog());

        // ✅ Подписка на выход из группы (оставлено из первой версии)
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
        // ✅ Используем аккуратную загрузку участников из первой версии
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
        new AlertDialog.Builder(requireContext())
                .setTitle("Код приглашения")
                .setMessage("Поделитесь этим кодом: " + InviteCodeUtil.encode(groupId))
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLeaveGroupDialog() {
        // ✅ Реализовано полноценное подтверждение выхода (без TODO)
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
}
