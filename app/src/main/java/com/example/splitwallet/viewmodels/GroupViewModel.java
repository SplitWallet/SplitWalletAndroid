package com.example.splitwallet.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.splitwallet.models.Group;
import com.example.splitwallet.repository.GroupRepository;

public class GroupViewModel extends ViewModel {
    private GroupRepository groupRepository = new GroupRepository();
    public MutableLiveData<Group> groupLiveData = new MutableLiveData<>();

    public void createGroup(String name) {
        groupRepository.createGroup(name, groupLiveData);
    }
}