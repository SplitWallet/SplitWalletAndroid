package com.example.splitwallet.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;

import java.util.List;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder> {
    private final List<Balance> balances;

    public BalanceAdapter(List<Balance> balances) {
        this.balances = balances;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        Balance balance = balances.get(position);
        holder.bind(balance);
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    static class BalanceViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;
        private final TextView tvYouOwe;
        private final TextView tvOwesYou;
        private final TextView tvNetBalance;

        public BalanceViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvYouOwe = itemView.findViewById(R.id.tvYouOwe);
            tvOwesYou = itemView.findViewById(R.id.tvOwesYou);
            tvNetBalance = itemView.findViewById(R.id.tvNetBalance);
        }

        public void bind(Balance balance) {
            tvUsername.setText(balance.getUsername());
            tvYouOwe.setText(String.format("You owe: %.2f", balance.getYouOwe()));
            tvOwesYou.setText(String.format("Owes you: %.2f", balance.getOwesYou()));

            double netBalance = balance.getNetBalance();
            tvNetBalance.setText(String.format("Net balance: %.2f", netBalance));

            // Подсветка в зависимости от баланса
            if (netBalance < 0) {
                tvNetBalance.setTextColor(itemView.getContext().getColor(R.color.colorNegative));
            } else if (netBalance > 0) {
                tvNetBalance.setTextColor(itemView.getContext().getColor(R.color.colorPositive));
            } else {
                tvNetBalance.setTextColor(itemView.getContext().getColor(R.color.colorNeutral));
            }
        }
    }
}