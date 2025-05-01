package com.example.splitwallet.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;
import com.example.splitwallet.models.User;

import java.util.ArrayList;
import java.util.List;

import com.example.splitwallet.models.UserResponse;

import lombok.Setter;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {
    private List<UserResponse> members = new ArrayList<>();
    @Setter
    private OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onClick(UserResponse member);
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        holder.bind(members.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(members.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<UserResponse> newMembers) {
        members = newMembers != null ? newMembers : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView, emailView, debtView;

        public MemberViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.memberName);
            emailView = itemView.findViewById(R.id.memberEmail);
            debtView = itemView.findViewById(R.id.memberDebt);
        }

        public void bind(UserResponse member) {
            nameView.setText(member.getName());
            emailView.setText(member.getEmail());
            debtView.setText("Баланс: 0 ₽"); // пока заглушка
        }
    }
}

