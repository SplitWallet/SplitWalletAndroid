package com.example.splitwallet.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;
import com.example.splitwallet.utils.InviteCodeUtil;
import com.example.splitwallet.viewmodels.GroupViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupDetailsActivity extends AppCompatActivity {
    private GroupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Получаем переданные данные
        Long groupId = getIntent().getLongExtra("GROUP_ID", -1);
        String groupName = getIntent().getStringExtra("GROUP_NAME");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Установка названия группы в тулбар
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(groupName); // <-- вот тут теперь название группы
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        // Настройка RecyclerView
        RecyclerView membersRecyclerView = findViewById(R.id.membersRecyclerView);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MembersAdapter adapter = new MembersAdapter();
        membersRecyclerView.setAdapter(adapter);

        // Загрузка участников группы
        if (groupId != -1) {
            String token = "Bearer " + getSharedPreferences("auth", MODE_PRIVATE).getString("token", "");
            viewModel.loadGroupMembers(groupId, token);

            viewModel.getGroupMembersLiveData().observe(this, members -> {
                if (members != null) {
                    adapter.updateMembers(members);
                } else {
                    Toast.makeText(this, "Ошибка загрузки участников", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Кнопка для добавления
        FloatingActionButton fabMain = findViewById(R.id.fabMain);
        fabMain.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, fabMain);
            popup.getMenu().add("Add payment");
            popup.getMenu().add("Add expense");
            popup.setOnMenuItemClickListener(item -> {
                Toast.makeText(this, item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
                return true;
            });
            popup.show();
        });

        adapter.setListener(member -> {
            Intent intent = new Intent(this, MemberDetailsActivity.class);
            intent.putExtra("memberName", member.getName());
            intent.putExtra("memberEmail", member.getEmail());
            intent.putExtra("debt", 0); // пока заглушка
            startActivity(intent);
        });

        findViewById(R.id.btnInvite).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Код приглашения")
                    .setMessage("Отправьте этот код другу: " + InviteCodeUtil.encode(groupId))
                    .setPositiveButton("ОК", null)
                    .show();
        });

        findViewById(R.id.btnLeaveGroup).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Покинуть группу")
                    .setMessage("Вы уверены?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        Toast.makeText(this, "Группа покинута", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

    }
}