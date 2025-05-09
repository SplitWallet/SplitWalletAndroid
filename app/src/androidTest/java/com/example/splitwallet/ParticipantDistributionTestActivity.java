package com.example.splitwallet;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ParticipantDistributionTestActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_test);
        recyclerView = findViewById(R.id.rvParticipants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}

