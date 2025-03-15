package com.example.splitwallet;

import com.example.splitwallet.models.Group;
import com.example.splitwallet.ui.LoginActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.splitwallet.viewmodels.GroupViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_create_group) {
                    showCreateGroupDialog();
                    return true;
                }
                return NavigationUI.onNavDestinationSelected(item, navController) || MainActivity.super.onOptionsItemSelected(item);
            }
        });

        groupViewModel.groupLiveData.observe(this, group -> {
            if (group != null) {
                Toast.makeText(this, "Group created: " + group.getName(), Toast.LENGTH_SHORT).show();
                addGroupToMenu(group);
            } else {
                Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
            }
        });
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

    // Метод для проверки авторизации
    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token != null; // Если токен есть, пользователь авторизован
    }

    private void showCreateGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Group");
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String groupName = input.getText().toString();
            if (!groupName.isEmpty()) {
                groupViewModel.createGroup(groupName);
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
        menu.add(R.id.nav_group_list, Menu.NONE, Menu.NONE, group.getName()).setIcon(R.drawable.ic_menu_gallery)
                .setOnMenuItemClickListener(item -> {
                    Toast.makeText(this, "Opening group: " + group.getName(), Toast.LENGTH_SHORT).show();
                    return true;
                });
    }

}