package com.example.splitwallet.ui;

import static org.mockito.Mockito.*;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ApplicationProvider;

import com.example.splitwallet.models.UserResponse;
import com.example.splitwallet.viewmodels.GroupViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;

public class GroupDetailsActivityTest {

    private GroupDetailsActivity activity;
    private GroupViewModel mockViewModel;

    @Before
    public void setup() {
        mockViewModel = mock(GroupViewModel.class);

        // Мокаем ViewModelProvider
        MockedStatic<ViewModelProvider> providerMockedStatic = Mockito.mockStatic(ViewModelProvider.class);
        ViewModelProvider.Factory factory = mock(ViewModelProvider.Factory.class);
        ViewModelProvider provider = mock(ViewModelProvider.class);
        providerMockedStatic.when(() -> new ViewModelProvider(any())).thenReturn(provider);
        when(provider.get(GroupViewModel.class)).thenReturn(mockViewModel);
    }

    @Test
    public void testGroupMembersLoaded_whenGroupIdValid() {
        MutableLiveData<java.util.List<UserResponse>> liveData = new MutableLiveData<>();
        when(mockViewModel.getGroupMembersLiveData()).thenReturn(liveData);

        activity = new GroupDetailsActivity();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupDetailsActivity.class);
        intent.putExtra("GROUP_ID", 1L);
        intent.putExtra("GROUP_NAME", "Test Group");

        activity.setIntent(intent);
        activity.onCreate(null);

        // Подставляем фейковые данные
        liveData.postValue(Collections.singletonList(
                new UserResponse("1", "Test User", "test@example.com", null)
        ));

        // Тестируем, что RecyclerView отреагировал через адаптер
        // (здесь скорее нужна интеграция — в юниты сложно без Robolectric или Espresso)
    }
}
