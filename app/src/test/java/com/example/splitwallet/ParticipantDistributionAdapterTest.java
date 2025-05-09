package com.example.splitwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.splitwallet.R;
import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.models.ParticipantDistributionAdapter;
import com.example.splitwallet.models.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class ParticipantDistributionAdapterTest {

    @Mock
    RecyclerView recyclerView;

    private List<User> participants;
    private List<ExpenseUser> expenseUsers;
    private ParticipantDistributionAdapter adapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Инициализация участников и их данных
        participants = new ArrayList<>();
        User u1 = new User();
        u1.setId("0");
        u1.setEmail("email1@gmail.com");
        u1.setPhoneNumber("123");
        u1.setUsername("Alice");
        User u2 = new User();
        u2.setId("1");
        u2.setEmail("email2@gmail.com");
        u2.setPhoneNumber("567");
        u2.setUsername("Bob");
        participants.add(u1);
        participants.add(u2);

        expenseUsers = new ArrayList<>();
        expenseUsers.add(new ExpenseUser("1", 50.0, 0.0));

        adapter = new ParticipantDistributionAdapter(participants, expenseUsers, 100.0, recyclerView);
    }

    @Test
    public void testAdapterInitialization() {
        assertNotNull(adapter);
        assertEquals(2, adapter.getItemCount());
        assertEquals("Alice", adapter.participantDataList.get(0).user.getName());
        assertEquals(0.0, adapter.participantDataList.get(0).amount, 0.001);
        assertEquals("Bob", adapter.participantDataList.get(1).user.getName());
        assertEquals(50.0, adapter.participantDataList.get(1).amount, 0.001);
    }

    @Test
    public void testGetUpdatedDistribution() {
        adapter.participantDataList.get(0).amount = 50.0;

        List<ExpenseUser> updatedDistribution = adapter.getUpdatedDistribution();

        assertEquals(1, updatedDistribution.size());
        assertEquals("1", updatedDistribution.get(0).getUserId());
        assertEquals(50.0, updatedDistribution.get(0).getAmount(), 0.001);
    }

    @Test
    public void testAdapterWithEmptyParticipants() {
        participants.clear(); // Убираем всех участников

        adapter = new ParticipantDistributionAdapter(participants, expenseUsers, 100.0, recyclerView);

        assertNotNull(adapter);
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFindExpenseUser() {
        ExpenseUser result = adapter.findExpenseUser(expenseUsers, "1");
        assertNotNull(result);
        assertEquals("1", result.getUserId());

        result = adapter.findExpenseUser(expenseUsers, "3");
        assertNull(result);
    }
}
