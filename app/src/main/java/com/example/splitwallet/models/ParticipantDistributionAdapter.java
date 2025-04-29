package com.example.splitwallet.models;

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
import java.util.List;

public class ParticipantDistributionAdapter extends RecyclerView.Adapter<ParticipantDistributionAdapter.ViewHolder> {
    private List<User> participants;
    private List<ExpenseUser> expenseUsers;
    private Double totalAmount;
    private RecyclerView participantsRecyclerView;
    private RecyclerView recyclerView;

    public ParticipantDistributionAdapter(List<User> participants, List<ExpenseUser> expenseUsers, Double totalAmount, RecyclerView recyclerView) {
        this.participants = participants != null ? participants : new ArrayList<>();
        this.expenseUsers = expenseUsers != null ? expenseUsers : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.recyclerView = recyclerView;
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
        User participant = participants.get(position);
        ExpenseUser expenseUser = findExpenseUser(participant.getId());

        holder.name.setText(participant.getName());
        holder.amount.setText(String.valueOf(expenseUser != null ? expenseUser.getAmount() : 0));

        // Логика для CheckBox и EditText
        holder.include.setChecked(expenseUser != null);
        if (expenseUser != null) {
            holder.amount.setText(String.valueOf(expenseUser.getAmount()));
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    private ExpenseUser findExpenseUser(String userId) {
        for (ExpenseUser eu : expenseUsers) {
            if (eu.getUserId().equals(userId)) {
                return eu;
            }
        }
        return null;
    }

    public List<ExpenseUser> getUpdatedDistribution() {
        List<ExpenseUser> result = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            ViewHolder holder = (ViewHolder) participantsRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null && holder.include.isChecked()) {
                ExpenseUser expenseUser = new ExpenseUser();
                expenseUser.setUserId(participants.get(i).getId());
                try {
                    expenseUser.setAmount(Double.parseDouble(holder.amount.getText().toString()));
                } catch (NumberFormatException e) {
                    expenseUser.setAmount(0.0);
                }
                expenseUser.setPaid(0.0); // По умолчанию 0, можно изменить
                result.add(expenseUser);
            }
        }
        return result;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox include;
        TextView name;
        EditText amount;

        public ViewHolder(View view) {
            super(view);
            include = view.findViewById(R.id.cbInclude);
            name = view.findViewById(R.id.tvName);
            amount = view.findViewById(R.id.etAmount);
        }
    }
}