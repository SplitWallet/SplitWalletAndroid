package com.example.splitwallet;

import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.JoinGroupFragment;
import com.example.splitwallet.ui.GroupExpensesActivity;
import com.example.splitwallet.ui.GroupDetailsActivity;
import com.example.splitwallet.ui.GroupPagerActivity;
import com.example.splitwallet.ui.JoinGroupActivity;
import com.example.splitwallet.ui.LoginActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitwallet.viewmodels.GroupViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.splitwallet.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private GroupViewModel groupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Проверка авторизации
        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Закрыть MainActivity
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "click", Toast.LENGTH_LONG).show();

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        groupViewModel = new ViewModelProvider(this).get(GroupViewModel.class);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_create_group) {
                showCreateGroupDialog();
                return true;
            }

            if (id == R.id.nav_join_group) {
                SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);

                Intent intent = new Intent(MainActivity.this, JoinGroupActivity.class);
                intent.putExtra("TOKEN", token); // <-- передаём токен
                startActivity(intent);
                return true;
            }
//            if (id == R.id.nav_join_group) {
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, new JoinGroupFragment()) // обязательно укажи id контейнера
//                        .addToBackStack(null) // чтобы можно было вернуться
//                        .commit();
//                return true;
//            }

            // Группы
            if (item.getGroupId() == R.id.nav_group_list) {
                Long groupId = (Long) item.getIntent().getSerializableExtra("GROUP_ID");
                openGroupDetails(groupId);
                return true;
            }

            // Остальное — навигация через NavigationUI
            return NavigationUI.onNavDestinationSelected(item, navController)
                    || super.onOptionsItemSelected(item);
        });


        groupViewModel.groupLiveData.observe(this, group -> {
            if (group != null) {
                Toast.makeText(this, "Group created: " + group.getName(), Toast.LENGTH_SHORT).show();
                addGroupToMenu(group);
            } else {
                Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            loadUserGroups(token);
        }

        groupViewModel.getUserGroupsLiveData().observe(this, groups -> {
            if (groups != null) {
                if (groups.isEmpty()) {
                    Toast.makeText(this, "You don't have any groups yet", Toast.LENGTH_SHORT).show();
                }
                updateGroupsMenu(groups);
            }else {
                Toast.makeText(this, "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });

//        // Обновляем обработчик меню
//        navigationView.setNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.nav_create_group) {
//                showCreateGroupDialog();
//                return true;
//            }
//            // Обработка выхода остается без изменений
//            return NavigationUI.onNavDestinationSelected(item, navController)
//                    || super.onOptionsItemSelected(item);
//        });

        groupViewModel.getGroupDeleted().observe(this, deleted -> {
            if (deleted != null && deleted) {
                Toast.makeText(this, "Группа удалена", Toast.LENGTH_SHORT).show();

                // Обновим список после удаления
                //SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token_ = sharedPreferences.getString("token", null);
                if (token_ != null) {
                    loadUserGroups(token_);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            new AlertDialog.Builder(this)
                    .setTitle("Выход из аккаунта")
                    .setMessage("Вы уверены, что хотите выйти?")
                    .setPositiveButton("Выйти", (dialog, which) -> logout(this))
                    .setNegativeButton("Отмена", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            loadUserGroups(token);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Обновляем список групп после присоединения
            SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);
            if (token != null) {
                loadUserGroups(token);
            }
        }
    }

    // Метод для проверки авторизации
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("AUTH_CHECK", "Токен: " + token);
        return token != null; // Если токен есть, пользователь авторизован
    }

    private void logout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth", MODE_PRIVATE);
        sharedPreferences.edit()
                .remove("token")
                .apply();

        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
        finish();

    }

    private void loadUserGroups(String token) {
        groupViewModel.loadUserGroups(token);
    }

    private void updateGroupsMenu(List<Group> groups) {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();

        // Удаляем старые группы
        menu.removeGroup(R.id.nav_group_list);

        for (Group group : groups) {
            MenuItem item = menu.add(R.id.nav_group_list, Menu.NONE, Menu.NONE, group.getName())
                    .setIcon(R.drawable.ic_group_icon);

            // Обычный клик — открыть
            item.setOnMenuItemClickListener(menuItem -> {
                openGroupExpenses(group.getId());
                return true;
            });

            // Длинный клик — удалить (непосредственно повесим позже)
        }

        // Задержка нужна, чтобы NavigationView успел отрисовать меню
        new Handler().postDelayed(() -> {
            for (int i = 0; i < navigationView.getChildCount(); i++) {
                View view = navigationView.getChildAt(i);

                if (view instanceof ViewGroup) {
                    findAndHookLongClicks((ViewGroup) view, groups);
                }
            }
        }, 100);
    }

    private void findAndHookLongClicks(ViewGroup parent, List<Group> groups) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                findAndHookLongClicks((ViewGroup) child, groups);
            } else if (child instanceof TextView) {
                TextView textView = (TextView) child;
                String name = textView.getText().toString();

                for (Group group : groups) {
                    if (group.getName().equals(name)) {

                        // Обычный клик — открытие группы
                        textView.setOnClickListener(v -> openGroupExpenses(group.getId()));

                        // Долгий клик — удаление
                        textView.setOnLongClickListener(v -> {
                            showDeleteGroupDialog(group);
                            return true; // важно вернуть true
                        });

                        break;
                    }
                }
            }
        }
    }

    private void showDeleteGroupDialog(Group group) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить группу?")
                .setMessage("Вы уверены, что хотите удалить группу \"" + group.getName() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", null);
                    if (token != null) {
                        groupViewModel.deleteGroup(group.getId(), token);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void openGroupDetails(Long groupId) {
        // 1. Получаем название группы из списка
        String groupName = "";
        List<Group> groups = groupViewModel.getUserGroupsLiveData().getValue();
        if (groups != null) {
            for (Group group : groups) {
                if (group.getId().equals(groupId)) {
                    groupName = group.getName();
                    break;
                }
            }
        }

        // 2. Создаем Intent для перехода
        Intent intent = new Intent(this, GroupDetailsActivity.class);
        intent.putExtra("GROUP_ID", groupId);
        intent.putExtra("GROUP_NAME", groupName);

        // 3. Запускаем Activity
        startActivity(intent);
    }
    private void openGroupExpenses(Long groupId) {
        // Получаем название группы
        String groupName = "";
        List<Group> groups = groupViewModel.getUserGroupsLiveData().getValue();
        if (groups != null) {
            for (Group group : groups) {
                if (group.getId().equals(groupId)) {
                    groupName = group.getName();
                    break;
                }
            }
        }

        Intent intent = new Intent(this, GroupPagerActivity.class);
        intent.putExtra("GROUP_ID", groupId);
        intent.putExtra("GROUP_NAME", groupName);
        startActivity(intent);
    }

    private void showCreateGroupDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            Toast.makeText(this, "Ошибка: токен не найден. Пожалуйста, войдите снова.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Group");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String groupName = input.getText().toString();
            if (!groupName.isEmpty()) {
                groupViewModel.createGroup(groupName, token);
            } else {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addGroupToMenu(Group group) {
        NavigationView navigationView = binding.navView;
        Menu menu = navigationView.getMenu();
        menu.add(R.id.nav_group_list, Menu.NONE, Menu.NONE, group.getName()).setIcon(R.drawable.ic_group_icon)
                .setOnMenuItemClickListener(item -> {
                    Toast.makeText(this, "Opening group: " + group.getName(), Toast.LENGTH_SHORT).show();
                    openGroupExpenses(group.getId());
                    return true;
                });
    }

    private void showDeleteGroupDialog(Long groupId, String groupName) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить группу")
                .setMessage("Вы уверены, что хотите удалить \"" + groupName + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", null);
                    if (token != null) {
                        groupViewModel.deleteGroup(groupId, token); // Добавим этот метод ниже
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

}