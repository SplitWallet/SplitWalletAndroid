package com.example.splitwallet.models;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.databinding.ItemExpenseBinding;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {
    public ExpenseAdapter() {
        super(new DiffUtil.ItemCallback<Expense>() {
            @Override
            public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExpenseBinding binding;

        ExpenseViewHolder(ItemExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Expense expense) {
            binding.tvName.setText(expense.getName());
            binding.tvAmount.setText(String.format("%s %.2f",
                    expense.getCurrency(), expense.getAmount()));
            binding.tvDate.setText(expense.getDate().toString());
            binding.tvDescription.setText(expense.getDescription());
        }
    }
}