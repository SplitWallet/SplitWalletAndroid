package com.example.splitwallet.models;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class GroupPagerAdapter extends FragmentStateAdapter {
    private final Long groupId;
    private final String groupName;

    public GroupPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                             Long groupId, String groupName) {
        super(fragmentActivity);
        this.groupId = groupId;
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ExpensesFragment.newInstance(groupId);
            case 1:
                return GroupDetailsFragment.newInstance(groupId, groupName);
            default:
                throw new IllegalArgumentException("Invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Два фрагмента
    }
}