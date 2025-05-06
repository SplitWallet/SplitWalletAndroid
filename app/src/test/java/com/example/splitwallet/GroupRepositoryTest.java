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
public class GroupRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ApiService mockApiService;

    @Mock
    private Call<Group> mockGroupCall;

    @Mock
    private Call<List<Group>> mockGroupsCall;

    @Mock
    private Call<List<User>> mockUsersCall;

    @Mock
    private Call<Void> mockVoidCall;

    @Captor
    private ArgumentCaptor<Callback<Group>> groupCallbackCaptor;

    @Captor
    private ArgumentCaptor<Callback<List<Group>>> groupsCallbackCaptor;

    @Captor
    private ArgumentCaptor<Callback<List<User>>> usersCallbackCaptor;

    @Captor
    private ArgumentCaptor<Callback<Void>> voidCallbackCaptor;

    private GroupRepository groupRepository;

    @Before
    public void setUp() {
        groupRepository = new GroupRepository(mockApiService);
    }

    @Test
    public void createGroup_success_shouldUpdateLiveData() {
        Group mockGroup = new Group();
        mockGroup.setId(1L);
        mockGroup.setName("Test Group");

        when(mockApiService.createGroup(anyString(), any(CreateGroupRequest.class)))
                .thenReturn(mockGroupCall);

        MutableLiveData<Group> liveData = new MutableLiveData<>();

        groupRepository.createGroup("Test Group", liveData, "token");

        verify(mockGroupCall).enqueue(groupCallbackCaptor.capture());
        groupCallbackCaptor.getValue().onResponse(mockGroupCall, Response.success(mockGroup));

        assertEquals("Test Group", liveData.getValue().getName());
    }

    @Test
    public void getUserGroups_shouldSortByUpdatedAtDesc() {
        Group oldGroup = new Group();
        oldGroup.setUpdatedAt(LocalDateTime.of(2023, 1, 1, 0, 0));

        Group newGroup = new Group();
        newGroup.setUpdatedAt(LocalDateTime.of(2023, 1, 2, 0, 0));

        List<Group> mockGroups = Arrays.asList(oldGroup, newGroup);

        when(mockApiService.getUserGroups(anyString()))
                .thenReturn(mockGroupsCall);

        MutableLiveData<List<Group>> liveData = new MutableLiveData<>();

        groupRepository.getUserGroups("token", liveData);

        verify(mockGroupsCall).enqueue(groupsCallbackCaptor.capture());
        groupsCallbackCaptor.getValue().onResponse(mockGroupsCall, Response.success(mockGroups));

        assertEquals(newGroup, liveData.getValue().get(0));
        assertEquals(oldGroup, liveData.getValue().get(1));
    }

    @Test
    public void getGroupMembers_success_shouldInvokeCallback() {
        User user1 = new User();
        user1.setUsername("Alice");

        User user2 = new User();
        user2.setUsername("Bob");

        List<User> mockMembers = Arrays.asList(user1, user2);

        when(mockApiService.getGroupMembers(anyLong(), anyString()))
                .thenReturn(mockUsersCall);

        GroupRepository.MembersCallback callback = mock(GroupRepository.MembersCallback.class);

        groupRepository.getGroupMembers(1L, "token", callback);

        verify(mockUsersCall).enqueue(usersCallbackCaptor.capture());
        usersCallbackCaptor.getValue().onResponse(mockUsersCall, Response.success(mockMembers));

        verify(callback).onSuccess(mockMembers);
        verify(callback, never()).onError(any());
    }

    @Test
    public void deleteGroup_success_shouldReturnTrue() {
        when(mockApiService.deleteGroup(anyString(), anyLong()))
                .thenReturn(mockVoidCall);

        GroupRepository.DeleteCallback callback = mock(GroupRepository.DeleteCallback.class);

        groupRepository.deleteGroup(1L, "token", callback);

        verify(mockVoidCall).enqueue(voidCallbackCaptor.capture());
        voidCallbackCaptor.getValue().onResponse(mockVoidCall, Response.success(null));

        verify(callback).onResponse(true, 200);
    }

//    @Test
//    public void createGroup_networkFailure_shouldSetLiveDataToNull() {
//        when(mockApiService.createGroup(anyString(), any(CreateGroupRequest.class)))
//                .thenReturn(mockGroupCall);
//
//        MutableLiveData<Group> liveData = new MutableLiveData<>();
//
//        groupRepository.createGroup("Test", liveData, "token");
//
//        verify(mockGroupCall).enqueue(groupCallbackCaptor.capture());
//        groupCallbackCaptor.getValue().onFailure(mockGroupCall, new IOException("No internet"));
//
//        assertNull(liveData.getValue());
//    }

    @Test
    public void leaveGroup_success_shouldInvokeCallback() {
        when(mockApiService.leaveGroup(anyLong(), anyString(), anyString()))
                .thenReturn(mockVoidCall);

        GroupRepository.LeaveGroupCallback callback = mock(GroupRepository.LeaveGroupCallback.class);

        groupRepository.leaveGroup(1L, "user123", "token", callback);

        verify(mockVoidCall).enqueue(voidCallbackCaptor.capture());
        voidCallbackCaptor.getValue().onResponse(mockVoidCall, Response.success(null));

        verify(callback).onResponse(true, 200);
    }
}
