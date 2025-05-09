package com.example.splitwallet;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.splitwallet.models.GroupDetailsFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GroupDetailsFragmentTest {

    @Test
    public void testGetAuthToken_returnsNull_ifNoToken() {
        FragmentScenario<GroupDetailsFragment> scenario =
                FragmentScenario.launchInContainer(GroupDetailsFragment.class);

        scenario.onFragment(fragment -> {
            SharedPreferences prefs = fragment.requireActivity()
                    .getSharedPreferences("auth", MODE_PRIVATE);
            prefs.edit().remove("token").apply();

            String token = fragment.getAuthToken();
            assertNull(token);
        });
    }

    @Test
    public void testExtractUserIdFromJwt_validToken_returnsUserId() {
        FragmentScenario<GroupDetailsFragment> scenario =
                FragmentScenario.launchInContainer(GroupDetailsFragment.class);

        scenario.onFragment(fragment -> {
            // Payload: {"sub":"12345"}
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSJ9.dummysignature";
            String userId = fragment.extractUserIdFromJwt(token);
            assertEquals("12345", userId);
        });
    }

    @Test
    public void testExtractUserIdFromJwt_invalidToken_returnsNull() {
        FragmentScenario<GroupDetailsFragment> scenario =
                FragmentScenario.launchInContainer(GroupDetailsFragment.class);

        scenario.onFragment(fragment -> {
            String token = "invalid.jwt.token";
            String userId = fragment.extractUserIdFromJwt(token);
            assertNull(userId);
        });
    }

    @Test
    public void testOnCreateView_initializesRecyclerView() {
        FragmentScenario<GroupDetailsFragment> scenario =
                FragmentScenario.launchInContainer(GroupDetailsFragment.class);

        scenario.onFragment(fragment -> {
            View view = fragment.requireView();
            RecyclerView recyclerView = view.findViewById(R.id.membersRecyclerView);
            assertNotNull(recyclerView);
        });
    }
}
