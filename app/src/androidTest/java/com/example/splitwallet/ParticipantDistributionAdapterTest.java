////package com.example.splitwallet;
////
////import static org.junit.Assert.assertEquals;
////import static org.junit.Assert.assertNotNull;
////import static org.junit.Assert.assertNull;
////import static org.junit.Assert.assertTrue;
////import static org.mockito.Mockito.mock;
////import static org.mockito.Mockito.when;
////
////import android.text.Editable;
////import android.text.TextWatcher;
////import android.widget.CheckBox;
////import android.widget.EditText;
////import android.widget.TextView;
////
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.example.splitwallet.R;
////import com.example.splitwallet.models.ExpenseUser;
////import com.example.splitwallet.models.ParticipantDistributionAdapter;
////import com.example.splitwallet.models.User;
////
////import org.junit.Before;
////import org.junit.Test;
////import org.mockito.Mock;
////import org.mockito.Mockito;
////import org.mockito.MockitoAnnotations;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class ParticipantDistributionAdapterTest {
////
////    @Mock
////    RecyclerView recyclerView;
////
////    private List<User> participants;
////    private List<ExpenseUser> expenseUsers;
////    private ParticipantDistributionAdapter adapter;
////
////    @Before
////    public void setUp() {
////        MockitoAnnotations.initMocks(this);
////
////        // Инициализация участников и их данных
////        participants = new ArrayList<>();
////        User u1 = new User();
////        u1.setId("0");
////        u1.setEmail("email1@gmail.com");
////        u1.setPhoneNumber("123");
////        u1.setUsername("Alice");
////        User u2 = new User();
////        u2.setId("1");
////        u2.setEmail("email2@gmail.com");
////        u2.setPhoneNumber("567");
////        u2.setUsername("Bob");
////        participants.add(u1);
////        participants.add(u2);
////
////        expenseUsers = new ArrayList<>();
////        expenseUsers.add(new ExpenseUser("1", 50.0, 0.0));
////
////        adapter = new ParticipantDistributionAdapter(participants, expenseUsers, 100.0, recyclerView);
////    }
////
////    @Test
////    public void testAdapterInitialization() {
////        assertNotNull(adapter);
////        assertEquals(2, adapter.getItemCount());
////        assertEquals("Alice", adapter.participantDataList.get(0).user.getName());
////        assertEquals(0.0, adapter.participantDataList.get(0).amount, 0.001);
////        assertEquals("Bob", adapter.participantDataList.get(1).user.getName());
////        assertEquals(50.0, adapter.participantDataList.get(1).amount, 0.001);
////    }
////
//////    @Test
//////    public void testIncludeCheckboxChangesAmount() {
//////        // Проверка начальной суммы
//////        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
//////        holder.name = mock(TextView.class);
//////        adapter.onBindViewHolder(holder, 0);
//////        assertEquals(0.0, adapter.participantDataList.get(0).amount, 0.001);
//////
//////        // Установка чекбокса в false и проверка суммы
//////        holder.include.setChecked(false);
//////        adapter.onBindViewHolder(holder, 0);
//////        assertEquals(0.0, adapter.participantDataList.get(0).amount, 0.001);
//////    }
////
////    @Test
////    public void testIncludeCheckboxChangesAmount() {
////        // Создание мока для ViewHolder
////        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
////
////        // Мокаем CheckBox
////        holder.include = mock(CheckBox.class);
////        when(holder.include.isChecked()).thenReturn(true); // Имитируем, что чекбокс выбран
////
////        // Выполняем действие, которое должно изменить состояние чекбокса
////        holder.include.setChecked(false);  // Снимаем флаг с чекбокса
////
////        // Проверяем, что чекбокс был снят
////        assertTrue("Checkbox should be unchecked", !holder.include.isChecked()); // Проверяем, что чекбокс снят
////    }
////
////
////
////    @Test
////    public void testAmountEditTextChanges() {
////        // Изменение текста в EditText
////        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
////        adapter.onBindViewHolder(holder, 0);
////        adapter.participantDataList.get(0).amount = 50.0;
////        adapter.onBindViewHolder(holder, 0);
////
////        assertEquals(50.0, adapter.participantDataList.get(0).amount, 0.001);
////    }
////
////    @Test
////    public void testGetUpdatedDistribution() {
////        adapter.participantDataList.get(0).amount = 50.0;
////
////        List<ExpenseUser> updatedDistribution = adapter.getUpdatedDistribution();
////
////        assertEquals(1, updatedDistribution.size());
////        assertEquals("1", updatedDistribution.get(0).getUserId());
////        assertEquals(50.0, updatedDistribution.get(0).getAmount(), 0.001);
////    }
////
////    @Test
////    public void testAdapterWithEmptyParticipants() {
////        participants.clear(); // Убираем всех участников
////
////        adapter = new ParticipantDistributionAdapter(participants, expenseUsers, 100.0, recyclerView);
////
////        assertNotNull(adapter);
////        assertEquals(0, adapter.getItemCount());
////    }
////
////    @Test
////    public void testFindExpenseUser() {
////        ExpenseUser result = adapter.findExpenseUser(expenseUsers, "1");
////        assertNotNull(result);
////        assertEquals("1", result.getUserId());
////
////        result = adapter.findExpenseUser(expenseUsers, "3");
////        assertNull(result);
////    }
////}
//
//package com.example.splitwallet;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.splitwallet.models.ExpenseUser;
//import com.example.splitwallet.models.ParticipantDistributionAdapter;
//import com.example.splitwallet.models.User;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ParticipantDistributionAdapterTest {
//
//    private ParticipantDistributionAdapter adapter;
//    private List<User> participants;
//    private List<ExpenseUser> expenseUsers;
//
//    @Before
//    public void setUp() {
//        participants = new ArrayList<>();
//        User u1 = new User("0", "email1@gmail.com", "123", "Alice");
//        User u2 = new User("1", "email2@gmail.com", "567", "Bob");
//        participants.add(u1);
//        participants.add(u2);
//
//        expenseUsers = new ArrayList<>();
//        expenseUsers.add(new ExpenseUser("1", 50.0, 0.0));
//
//        // Инициализируем адаптер
//        adapter = new ParticipantDistributionAdapter(participants, expenseUsers, 100.0, mock(RecyclerView.class));
//    }
//
//    @Test
//    public void testIncludeCheckboxChangesAmount() {
//        // Создаем моки для UI-элементов
//        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
//        holder.include = mock(CheckBox.class);
//        holder.name = mock(TextView.class);
//        holder.amount = mock(EditText.class);
//
//        // Настроим поведение чекбокса
//        when(holder.include.isChecked()).thenReturn(true);  // Имитируем, что чекбокс выбран
//
//        // Устанавливаем слушатель для чекбокса и проверяем изменение данных
//        holder.include.setChecked(false);  // Снимаем флаг с чекбокса
//        adapter.onBindViewHolder(holder, 0);  // Привязываем данные
//
//        // Проверяем, что сумма для пользователя Alice стала 0.0
//        assertEquals(0.0, adapter.participantDataList.get(0).amount, 0.001);
//    }
//
//    @Test
//    public void testAmountEditTextChanges() {
//        // Создаем моки для UI-элементов
//        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
//        holder.include = mock(CheckBox.class);
//        holder.name = mock(TextView.class);
//        holder.amount = mock(EditText.class);
//
//        // Устанавливаем начальную сумму для Alice
//        adapter.onBindViewHolder(holder, 0);
//        adapter.participantDataList.get(0).amount = 50.0;
//        adapter.onBindViewHolder(holder, 0);
//
//        // Проверяем, что сумма для Alice теперь 50.0
//        assertEquals(50.0, adapter.participantDataList.get(0).amount, 0.001);
//    }
//
//    @Test
//    public void testAmountChangesOnEditTextUpdate() {
//        // Создаем моки для UI-элементов
//        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
//        holder.include = mock(CheckBox.class);
//        holder.name = mock(TextView.class);
//        holder.amount = mock(EditText.class);
//
//        // Имитируем изменение текста в поле EditText
//        TextWatcher textWatcher = mock(TextWatcher.class);
//        holder.amount.addTextChangedListener(textWatcher);
//
//        // После изменения текста на 20.0 проверяем, что сумма обновилась
//        Editable editable = mock(Editable.class);
//        when(editable.toString()).thenReturn("0.0");
//        textWatcher.afterTextChanged(editable);
//
//        adapter.onBindViewHolder(holder, 0);
//
//        assertEquals(0.0, adapter.participantDataList.get(0).amount, 0.001);
//    }
//
//    @Test
//    public void testGetUpdatedDistribution() {
//        // Меняем сумму для Alice
//        adapter.participantDataList.get(0).amount = 50.0;
//
//        // Получаем обновленное распределение
//        List<ExpenseUser> updatedDistribution = adapter.getUpdatedDistribution();
//
//        // Проверяем, что обновление правильно отразилось на списке распределений
//        assertEquals(1, updatedDistribution.size());
//        assertEquals("1", updatedDistribution.get(0).getUserId());
//        assertEquals(50.0, updatedDistribution.get(0).getAmount(), 0.001);
//    }
//
//    @Test
//    public void testCheckboxStatePersistsAfterBinding() {
//        // Создаем моки для UI-элементов
//        ParticipantDistributionAdapter.ViewHolder holder = mock(ParticipantDistributionAdapter.ViewHolder.class);
//        holder.include = mock(CheckBox.class);
//        holder.name = mock(TextView.class);
//        holder.amount = mock(EditText.class);
//
//        // Ставим флаг на чекбокс
//        holder.include.setChecked(true);
//        adapter.onBindViewHolder(holder, 0);
//
//        // Проверяем, что состояние чекбокса сохранено после биндинга
//        assertTrue(holder.include.isChecked());
//    }
//}

package com.example.splitwallet;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.splitwallet.models.ExpenseUser;
import com.example.splitwallet.models.ParticipantDistributionAdapter;
import com.example.splitwallet.models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class ParticipantDistributionAdapterTest {

    @Rule
    public ActivityScenarioRule<ParticipantDistributionTestActivity> activityRule =
            new ActivityScenarioRule<>(ParticipantDistributionTestActivity.class);

    private List<User> participants;
    private List<ExpenseUser> expenseUsers;

    @Before
    public void setUp() {
        participants = new ArrayList<>();
        User u1 = new User(); u1.setId("0"); u1.setUsername("Alice");
        User u2 = new User(); u2.setId("1"); u2.setUsername("Bob");
        participants.add(u1); participants.add(u2);

        expenseUsers = new ArrayList<>();
        expenseUsers.add(new ExpenseUser("1", 50.0, 0.0));
    }

    @Test
    public void testAdapterBindsDataCorrectly() {
        activityRule.getScenario().onActivity(activity -> {
            ParticipantDistributionAdapter adapter = new ParticipantDistributionAdapter(
                    participants, expenseUsers, 100.0, activity.recyclerView
            );

            activity.recyclerView.setAdapter(adapter);

            // Проверяем первый элемент
            RecyclerView.ViewHolder vh = adapter.onCreateViewHolder(activity.recyclerView, 0);
            adapter.onBindViewHolder((ParticipantDistributionAdapter.ViewHolder) vh, 0);

            ParticipantDistributionAdapter.ViewHolder holder = (ParticipantDistributionAdapter.ViewHolder) vh;

            assertEquals("Alice", holder.name.getText().toString());
            assertEquals("0.0", holder.amount.getText().toString());
            assertFalse(holder.include.isChecked());
        });
    }
}
