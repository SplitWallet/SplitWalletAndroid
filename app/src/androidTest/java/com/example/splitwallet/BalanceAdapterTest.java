package com.example.splitwallet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.test.core.app.ApplicationProvider;

import com.example.splitwallet.models.Balance;
import com.example.splitwallet.models.BalanceAdapter;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.junit.runners.JUnit4;

import com.example.splitwallet.models.BalanceAdapter;
import com.example.splitwallet.models.Balance;

import java.util.List;

@RunWith(JUnit4.class)
public class BalanceAdapterTest {

    private Context context;
    private RecyclerView recyclerView;
    private BalanceAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Test
    public void testBindViewHolder_setsCorrectData() {
        List<Balance> balances = List.of(
                new Balance("Alice", 100.0, 0.0),
                new Balance("Bob", 0.0, 50.0),
                new Balance("Charlie", 10.0, 10.0)
        );
        adapter = new BalanceAdapter(balances);
        recyclerView.setAdapter(adapter);

        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(
                new FrameLayout(context), 0
        );
        adapter.onBindViewHolder((BalanceAdapter.BalanceViewHolder) viewHolder, 0);

        TextView tvUsername = viewHolder.itemView.findViewById(R.id.tvUsername);
        assertEquals("Alice", tvUsername.getText().toString());
    }
}

