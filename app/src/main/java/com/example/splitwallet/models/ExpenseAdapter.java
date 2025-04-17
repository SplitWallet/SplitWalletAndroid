package com.example.splitwallet.models;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.databinding.ItemExpenseBinding;

import java.util.HashMap;
import java.util.Map;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {
    private Map<String, User> membersMap = new HashMap<>();
    private static OnExpenseClickListener listener;

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public ExpenseAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.listener = listener;
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

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
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

            // Отображаем имя пользователя вместо ID
            if (expense.getUserWhoCreatedId() != null) {
                Log.d("DEBUG", "Looking for user ID: " + expense.getUserWhoCreatedId());
                Log.d("DEBUG", "Available IDs: " + membersMap.keySet());
                User creator = membersMap.get(expense.getUserWhoCreatedId());
                Log.d("USER_DEBUG", "Creator object: " + creator); // Проверьте весь объект
                Log.d("USER_DEBUG", "Creator name: " + (creator != null ? creator.getName() : "null"));
                String creatorName = creator != null ? creator.getName() : "ID: " + expense.getUserWhoCreatedId();
                binding.tvCreatedBy.setText(String.format("Добавил: %s", creatorName));
            } else {
                binding.tvCreatedBy.setText("Добавил: Неизвестно");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExpenseClick(expense);
                }
            });
        }
    }
    public void setMembersMap(Map<String, User> membersMap) {
        this.membersMap = membersMap != null ? membersMap : new HashMap<>();
        Log.d("ADAPTER", "Members map updated. Size: " + this.membersMap.size());
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Expense>() {
                @Override
                public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.equals(newItem);
                }
            };
}