package com.example.splitwallet.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.splitwallet.R;
import com.example.splitwallet.models.GroupPagerAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupPagerActivity extends AppCompatActivity {
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pager);

        Long groupId = getIntent().getLongExtra("GROUP_ID", -1);
        String groupName = getIntent().getStringExtra("GROUP_NAME");

        // Получаем Toolbar из макета
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(groupName);
        }

        // Настройка ViewPager
        viewPager = findViewById(R.id.view_pager);
        GroupPagerAdapter pagerAdapter = new GroupPagerAdapter(this, groupId, groupName);
        viewPager.setAdapter(pagerAdapter);

        // Связываем TabLayout с ViewPager
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Expenses" : "Details")
        ).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}