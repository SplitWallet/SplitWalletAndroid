package com.example.splitwallet;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.splitwallet.models.ExpensesFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExpensesFragmentTest {

    @Rule
    public ActivityScenarioRule<TestActivity> activityRule =
            new ActivityScenarioRule<>(TestActivity.class);

    @Test
    public void testAddExpenseDialogShownOnFabClick() {
        activityRule.getScenario().onActivity(activity -> {
            ExpensesFragment fragment = ExpensesFragment.newInstance(1L);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitNow();
        });

        // Нажимаем FAB
        onView(withId(R.id.fabMain)).perform(click());

        // Выбираем "Add Manually" из PopupMenu
        onView(withText("Manual Entry"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());


        // Проверяем, что открылся диалог
        onView(withText("Add New Expense")).check(matches(isDisplayed()));
    }
}

