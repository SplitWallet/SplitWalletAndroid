//package com.example.splitwallet;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.mock;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//
//import com.example.splitwallet.models.ExpensesFragment;
//import com.example.splitwallet.models.GroupDetailsFragment;
//import com.example.splitwallet.models.GroupPagerAdapter;
//
//import org.junit.Test;
//
//public class GroupPagerAdapterTest {
//
//    @Test
//    public void testItemCount_returnsTwo() {
//        FragmentActivity activity = mock(FragmentActivity.class);
//        GroupPagerAdapter adapter = new GroupPagerAdapter(activity, 1L, "TestGroup");
//
//        assertEquals(2, adapter.getItemCount());
//    }
//
//    @Test
//    public void testCreateFragment_returnsCorrectFragment() {
//        FragmentActivity activity = mock(FragmentActivity.class);
//        GroupPagerAdapter adapter = new GroupPagerAdapter(activity, 1L, "TestGroup");
//
//        Fragment frag0 = adapter.createFragment(0);
//        Fragment frag1 = adapter.createFragment(1);
//
//        assertTrue(frag0 instanceof ExpensesFragment);
//        assertTrue(frag1 instanceof GroupDetailsFragment);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testCreateFragment_invalidPosition_throwsException() {
//        FragmentActivity activity = mock(FragmentActivity.class);
//        GroupPagerAdapter adapter = new GroupPagerAdapter(activity, 1L, "TestGroup");
//
//        adapter.createFragment(2); // invalid position
//    }
//}
//

package com.example.splitwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.splitwallet.models.ExpensesFragment;
import com.example.splitwallet.models.GroupDetailsFragment;
import com.example.splitwallet.models.GroupPagerAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class GroupPagerAdapterTest {

    @Test
    public void testItemCount_returnsTwo() {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).setup().get();
        GroupPagerAdapter adapter = new GroupPagerAdapter(activity, 1L, "TestGroup");

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testCreateFragment_returnsCorrectFragment() {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).setup().get();
        GroupPagerAdapter adapter = new GroupPagerAdapter(activity, 1L, "TestGroup");

        Fragment frag0 = adapter.createFragment(0);
        Fragment frag1 = adapter.createFragment(1);

        assertTrue(frag0 instanceof ExpensesFragment);
        assertTrue(frag1 instanceof GroupDetailsFragment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateFragment_invalidPosition_throwsException() {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).setup().get();
        GroupPagerAdapter adapter = new GroupPagerAdapter(activity, 1L, "TestGroup");

        adapter.createFragment(2); // invalid position
    }
}
