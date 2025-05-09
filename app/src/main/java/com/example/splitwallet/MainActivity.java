
package com.example.splitwallet;

import com.example.splitwallet.models.Group;
import com.example.splitwallet.ui.GroupDetailsActivity;
import com.example.splitwallet.ui.GroupPagerActivity;
import com.example.splitwallet.ui.JoinGroupActivity;
import com.example.splitwallet.ui.LoginActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splitwallet.viewmodels.GroupViewModel;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.splitwallet.databinding.ActivityMainBinding;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private GroupViewModel groupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        // Проверка авторизации
        if (!isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Закрыть MainActivity
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView usernameView = headerView.findViewById(R.id.nav_header_username);
        TextView emailView = headerView.findViewById(R.id.nav_header_email);

        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        checkAndRequestNotificationPermission();
        createNotificationChannel();


        if (token != null) {
            Pair<String, String> userData = parseJwt(token); // логин, email
            if (userData != null) {
                usernameView.setText(userData.first);
                emailView.setText(userData.second);
            }
        }


        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        //NavigationView navigationView = binding.navView;
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
                Intent intent = new Intent(MainActivity.this, JoinGroupActivity.class);
                intent.putExtra("TOKEN", token); // <-- передаём токен
                startActivity(intent);
                return true;
            }

            return false;
        });

        groupViewModel.groupLiveData.observe(this, group -> {
                if (group != null) {
                    Toast.makeText(this, "Group created: " + group.getName(), Toast.LENGTH_SHORT).show();
                    //addGroupToMenu(group);
                    loadUserGroups(sharedPreferences.getString("token", null));
                } else {
                    Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
                }
        });

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


        groupViewModel.getGroupDeleted().observe(this, deleted -> {
            if (deleted != null && deleted) {
                Toast.makeText(this, "Группа удалена", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPrefs = getSharedPreferences("auth", MODE_PRIVATE);
                String authToken = sharedPrefs.getString("token", null);
                if (authToken != null) {
                    loadUserGroups(authToken);
                }

            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expenses Notifications";
            String description = "Notifications about new expenses in groups";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("expenses_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    // Константа для идентификатора запроса
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    // Метод для проверки и запроса разрешения
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Объяснение перед запросом (опционально)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    // Показать объяснение (диалог или Snackbar)
                    new AlertDialog.Builder(this)
                            .setTitle("Нужно разрешение")
                            .setMessage("Для показа уведомлений о расходах нужно дать разрешение")
                            .setPositiveButton("OK", (dialog, which) -> {
                                requestPermission();
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                } else {
                    requestPermission();
                }
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                NOTIFICATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted");
            } else {
                Log.d("MainActivity", "Notification permission denied");
                // Можно показать сообщение, что функционал уведомлений будет ограничен
            }
        }
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
        LoginActivity.signOutFromGoogle(context);
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

        // Полностью очищаем группу
        menu.removeGroup(R.id.nav_group_list);

        // Добавляем все группы, включая новосозданные
        for (Group group : groups) {
            addGroupToMenu(menu, group);
        }
    }

    private void addGroupToMenu(Menu menu, Group group) {
        // Проверяем, не добавлена ли уже эта группа
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getTitle() != null && item.getTitle().equals(group.getName())) {
                return; // Группа уже есть в меню
            }
        }

        MenuItem item = menu.add(R.id.nav_group_list, Menu.NONE, Menu.NONE, "")
                .setActionView(R.layout.menu_group_item);

        View actionView = item.getActionView();
        if (actionView != null) {
            TextView groupName = actionView.findViewById(R.id.group_name);
            ImageView groupIcon = actionView.findViewById(R.id.group_icon);
            ImageButton deleteBtn = actionView.findViewById(R.id.delete_btn);

            groupName.setText(group.getName());
            groupIcon.setImageResource(R.drawable.ic_group_icon);

            actionView.setOnClickListener(v -> openGroupExpenses(group.getId()));
            deleteBtn.setOnClickListener(v -> showDeleteGroupDialog(group));
        }
    }

    private void openGroupExpensesByName(String groupName) {
        List<Group> groups = groupViewModel.getUserGroupsLiveData().getValue();
        if (groups != null) {
            for (Group group : groups) {
                if (group.getName().equals(groupName)) {
                    openGroupExpenses(group.getId());
                    return;
                }
            }
        }
        Toast.makeText(this, "Группа не найдена", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteGroupDialog(Group group) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить группу?")
                .setMessage("Вы уверены, что хотите удалить группу \"" + group.getName() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    deleteGroup(group);
                })
                .setNegativeButton("Отмена", null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

//    private void deleteGroup(Group group) {
//        SharedPreferences sharedPrefs = getSharedPreferences("auth", MODE_PRIVATE);
//        String token = sharedPrefs.getString("token", null);
//
//        if (token != null) {
//            // 1. Вызываем удаление группы
//            groupViewModel.deleteGroup(group.getId(), token);
//
//            // 2. Наблюдаем за изменением статуса удаления
//            groupViewModel.getGroupDeleted().observe(this, success -> {
//                if (success != null) {
//                    if (success) {
//                        Toast.makeText(this, "Группа удалена", Toast.LENGTH_SHORT).show();
//
//                        // Перезагружаем список групп
//                        loadUserGroups(token);
//
//                        // Удаляем observer, чтобы избежать утечек памяти
//                        groupViewModel.getGroupDeleted().removeObservers(this);
//                    } else {
//                        Toast.makeText(this, "Ошибка при удалении группы", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }

    private void deleteGroup(Group group) {
        SharedPreferences sharedPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPrefs.getString("token", null);

        if (token != null) {
            groupViewModel.deleteGroup(group.getId(), token);

            groupViewModel.getGroupDeleted().observe(this, success -> {
                if (success != null) {
                    int statusCode = groupViewModel.getLastDeleteResponseCode();

                    if (success) {
                        handleDeleteSuccess();
                    } else {
                        handleDeleteError(statusCode);
                    }

                    groupViewModel.getGroupDeleted().removeObservers(this);
                }
            });
        }
    }

    private void handleDeleteSuccess() {
        Toast.makeText(this, "Группа удалена", Toast.LENGTH_SHORT).show();
        loadUserGroups(getSharedPreferences("auth", MODE_PRIVATE).getString("token", null));
    }

    private void handleDeleteError(int statusCode) {
        String errorMessage;
        switch (statusCode) {
            case 400:
                errorMessage = "Группу может удалить только её владелец";
                break;
            case 401:
                errorMessage = "Ошибка авторизации";
                logout(this);
                break;
            case 404:
                errorMessage = "Группа не найдена или уже присоединены";
                break;
            case -1:
                errorMessage = "Ошибка сети. Проверьте подключение";
                break;
            default:
                errorMessage = "Ошибка при удалении (код: " + statusCode + ")";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
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

    public static Pair<String, String> parseJwt(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            if (parts.length != 3) return null;

            byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE);
            String payloadJson = new String(decodedBytes, StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadJson);

            String login = payload.optString("preferred_username", "User");
            String email = payload.optString("email", "user@email.com");

            return new Pair<>(login, email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
