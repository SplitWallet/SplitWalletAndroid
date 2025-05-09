package com.example.splitwallet;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.action.ViewActions.click;

import com.example.splitwallet.R;
import com.example.splitwallet.models.UserResponse;
import com.example.splitwallet.ui.GroupDetailsActivity;
import com.example.splitwallet.ui.MembersAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class GroupDetailsActivityTest {

    @Test
    public void testToolbarTitleIsSet() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupDetailsActivity.class);
        intent.putExtra("GROUP_ID", 1L);
        intent.putExtra("GROUP_NAME", "My Test Group");
        ActivityScenario.launch(intent);

        try (ActivityScenario<GroupDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Проверка, что заголовок тулбара отображается
            Espresso.onView(ViewMatchers.withText("My Test Group"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testClickingMemberOpensMemberDetails() {

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupDetailsActivity.class);
        intent.putExtra("GROUP_ID", 1L);
        intent.putExtra("GROUP_NAME", "My Test Group");

        try (ActivityScenario<GroupDetailsActivity> scenario = ActivityScenario.launch(intent)) {

            scenario.onActivity(activity -> {
                RecyclerView recyclerView = activity.findViewById(R.id.membersRecyclerView);
                MembersAdapter adapter = (MembersAdapter) recyclerView.getAdapter();

                List<UserResponse> fakeUsers = new ArrayList<>();
                fakeUsers.add(new UserResponse("1", "Test User", "test@example.com", "123456789"));
                adapter.updateMembers(fakeUsers);
            });

            // Теперь кликаем по первому элементу — он точно есть
            Espresso.onView(ViewMatchers.withId(R.id.membersRecyclerView))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

            Espresso.onView(ViewMatchers.withText("Баланс: 0₽"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }


    @Test
    public void testPopupMenuAppears() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupDetailsActivity.class);
        intent.putExtra("GROUP_ID", 1L);
        intent.putExtra("GROUP_NAME", "My Test Group");
        ActivityScenario.launch(intent);


        try (ActivityScenario<GroupDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(ViewMatchers.withId(R.id.fabMain))
                    .perform(androidx.test.espresso.action.ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Add payment"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void testLeaveGroupDialogAppears() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupDetailsActivity.class);
        intent.putExtra("GROUP_ID", 1L);
        intent.putExtra("GROUP_NAME", "My Test Group");
        ActivityScenario.launch(intent);

        try (ActivityScenario<GroupDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(ViewMatchers.withId(R.id.btnLeaveGroup))
                    .perform(androidx.test.espresso.action.ViewActions.click());

            Espresso.onView(ViewMatchers.withText("Покинуть группу"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }
}

