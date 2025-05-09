package com.example.splitwallet.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticipantDistributionAdapter extends RecyclerView.Adapter<ParticipantDistributionAdapter.ViewHolder> {
    private final List<User> participants;
    public final List<ParticipantData> participantDataList;
    private final double totalAmount;

    public ParticipantDistributionAdapter(List<User> participants,
                                          List<ExpenseUser> expenseUsers,
                                          double totalAmount,
                                          RecyclerView recyclerView) {
        this.participants = participants != null ? participants : new ArrayList<>();
        this.totalAmount = totalAmount;

        // Инициализируем данные участников
        this.participantDataList = new ArrayList<>();
        for (User participant : this.participants) {
            ExpenseUser existing = findExpenseUser(expenseUsers, participant.getId());
            participantDataList.add(new ParticipantData(
                    participant,
                    existing != null,
                    existing != null ? existing.getAmount() : 0
            ));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant_distribution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParticipantData data = participantDataList.get(position);

        holder.name.setText(data.user.getName());
        holder.include.setChecked(data.isIncluded);
        holder.amount.setText(String.valueOf(data.amount));

        // Удаляем, чтобы избежать дублирования
        holder.include.setOnCheckedChangeListener(null);
        holder.amount.removeTextChangedListener(holder.textWatcher);

        holder.include.setOnCheckedChangeListener((buttonView, isChecked) -> {
            data.isIncluded = isChecked;
            if (!isChecked) {
                data.amount = 0;
                holder.amount.setText("0");
            }
        });

        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    data.amount = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    data.amount = 0;
                }
            }
        };
        holder.amount.addTextChangedListener(holder.textWatcher);
    }

    @Override
    public int getItemCount() {
        return this.participantDataList.size();
    }

    public List<ExpenseUser> getUpdatedDistribution() {
        List<ExpenseUser> result = new ArrayList<>();
        for (ParticipantData data : participantDataList) {
            if (data.isIncluded) {
                result.add(new ExpenseUser(
                        data.user.getId(),
                        data.amount,
                        0.0
                ));
            }
        }
        return result;
    }

    public ExpenseUser findExpenseUser(List<ExpenseUser> expenseUsers, String userId) {
        if (expenseUsers == null) return null;
        for (ExpenseUser eu : expenseUsers) {
            if (eu.getUserId().equals(userId)) {
                return eu;
            }
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox include;
        public TextView name;
        public EditText amount;
        TextWatcher textWatcher;

        public ViewHolder(View view) {
            super(view);
            include = view.findViewById(R.id.cbInclude);
            name = view.findViewById(R.id.tvName);
            amount = view.findViewById(R.id.etAmount);
        }
    }

    public static class ParticipantData {
        public User user;
        boolean isIncluded;
        public double amount;

        ParticipantData(User user, boolean isIncluded, double amount) {
            this.user = user;
            this.isIncluded = isIncluded;
            this.amount = amount;
        }
    }
}