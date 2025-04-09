package com.example.splitwallet.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.splitwallet.R;

public class MemberDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_details);

        ((TextView) findViewById(R.id.tvName)).setText(getIntent().getStringExtra("memberName"));
        ((TextView) findViewById(R.id.tvEmail)).setText(getIntent().getStringExtra("memberEmail"));
        ((TextView) findViewById(R.id.tvDebt)).setText("Баланс: " + getIntent().getIntExtra("debt", 0) + "₽");
    }
}

