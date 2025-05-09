package com.example.splitwallet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.splitwallet.api.ApiService;
import com.example.splitwallet.api.RetrofitClient;
import com.example.splitwallet.models.CreateGroupRequest;
import com.example.splitwallet.models.Group;
import com.example.splitwallet.models.User;
import com.example.splitwallet.models.UserResponse;
import com.example.splitwallet.repository.GroupRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import java.time.LocalDateTime;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


@RunWith(MockitoJUnitRunner.class)
//@RunWith(RobolectricTestRunner.class)
public class GroupRepositoryTest {

    @Mock ApiService apiService;
    @Mock Call<Group> groupCall;
    @Mock Call<List<Group>> groupListCall;
    @Mock Call<Void> voidCall;
    @Mock Call<List<User>> userCall;
    @Mock Call<Group> groupByIdCall;

    @InjectMocks GroupRepository groupRepository;

    @Captor ArgumentCaptor<Callback<Group>> groupCallbackCaptor;
    @Captor ArgumentCaptor<Callback<List<Group>>> groupListCaptor;
    @Captor ArgumentCaptor<Callback<Void>> voidCaptor;
    @Captor ArgumentCaptor<Callback<List<User>>> userCaptor;

    private MutableLiveData<Group> groupLiveData;
    private MutableLiveData<List<Group>> groupsLiveData;

    @Before
    public void setup() {
        groupLiveData = new MutableLiveData<>();
        groupsLiveData = new MutableLiveData<>();
    }

    @javax.annotation.Generated("excluded from coverage")
    @Test
    public void createGroup_success() {
        when(apiService.createGroup(any(), any())).thenReturn(groupCall);
        groupRepository.createGroup("Test Group", groupLiveData, "token");
        verify(groupCall).enqueue(groupCallbackCaptor.capture());

        Group group = new Group();
        groupCallbackCaptor.getValue().onResponse(groupCall, Response.success(group));

        assertEquals(group, groupLiveData.getValue());
    }

    @javax.annotation.Generated("excluded from coverage")
    @Test
    public void createGroup_failure() {
        when(apiService.createGroup(any(), any())).thenReturn(groupCall);
        groupRepository.createGroup("Test Group", groupLiveData, "token");
        verify(groupCall).enqueue(groupCallbackCaptor.capture());

        groupCallbackCaptor.getValue().onResponse(groupCall, Response.error(400, ResponseBody.create(null, "")));
        assertNull(groupLiveData.getValue());
    }

    @javax.annotation.Generated("excluded from coverage")
    @Test
    public void getUserGroups_success() {
        List<Group> groupList = Arrays.asList(new Group(), new Group());
        when(apiService.getUserGroups(any())).thenReturn(groupListCall);
        groupRepository.getUserGroups("token", groupsLiveData);
        verify(groupListCall).enqueue(groupListCaptor.capture());

        groupListCaptor.getValue().onResponse(groupListCall, Response.success(groupList));
        assertEquals(groupList, groupsLiveData.getValue());
    }

    @Test
    public void deleteGroup_success() {
        when(apiService.deleteGroup(any(), anyLong())).thenReturn(voidCall);
        GroupRepository.DeleteCallback callback = mock(GroupRepository.DeleteCallback.class);
        groupRepository.deleteGroup(1L, "token", callback);
        verify(voidCall).enqueue(voidCaptor.capture());

        voidCaptor.getValue().onResponse(voidCall, Response.success(null));
        verify(callback).onResponse(true, 200);
    }

    @Test
    public void deleteGroup_failure() {
        when(apiService.deleteGroup(any(), anyLong())).thenReturn(voidCall);
        GroupRepository.DeleteCallback callback = mock(GroupRepository.DeleteCallback.class);
        groupRepository.deleteGroup(1L, "token", callback);
        verify(voidCall).enqueue(voidCaptor.capture());

        voidCaptor.getValue().onFailure(voidCall, new Throwable("error"));
        verify(callback).onResponse(false, -1);
    }
}
