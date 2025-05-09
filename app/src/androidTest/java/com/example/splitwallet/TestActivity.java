package com.example.splitwallet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.splitwallet.models.ExpensesFragment;

public class TestActivity extends AppCompatActivity {

    public static final String EXTRA_GROUP_ID = "group_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (savedInstanceState == null) {
            long groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
            Fragment fragment = ExpensesFragment.newInstance(groupId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
