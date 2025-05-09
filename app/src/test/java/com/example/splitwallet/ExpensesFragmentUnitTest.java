package com.example.splitwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import android.content.Context;

import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.ExpensesFragment;
import com.example.splitwallet.viewmodels.ExpenseViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ExpensesFragmentUnitTest {

    @Mock
    Context mockContext;

    @Mock
    ExpenseViewModel mockViewModel;
    private ExpensesFragment fragment;

    @Before
    public void setUp() {
        fragment = new ExpensesFragment();
        fragment.expenseViewModel = mockViewModel;
    }

    @Test
    public void testParseQRParameters() throws Exception {
        String qrData = "t=20250405T1853&s=19837.00&fn=123456&i=789&fp=98765&n=1";
        Method method = ExpensesFragment.class.getDeclaredMethod("parseQRParameters", String.class);
        method.setAccessible(true);

        Map<String, String> result = (Map<String, String>) method.invoke(fragment, qrData);

        assertEquals("20250405T1853", result.get("t"));
        assertEquals("19837.00", result.get("s"));
        assertEquals("123456", result.get("fn"));
    }

//    @Test
//    public void testParseQRDataWithValidQR() {
//        String qrData = "t=20250405T1853&s=19837.00&fn=123456&i=789&fp=98765&n=1";
//
//        fragment.parseQRData(qrData);
//
//        ArgumentCaptor<CreateExpenseRequest> captor = ArgumentCaptor.forClass(CreateExpenseRequest.class);
//
//        verify(mockViewModel).createExpense(eq(fragment.groupId), captor.capture(), eq("test_token"));
//
//        CreateExpenseRequest request = captor.getValue();
//        assertEquals("Чек 123456-789", request.getName());
//        assertEquals("Оплата по чеку", request.getDescription());
//        assertEquals(19837.00, request.getAmount(), 0.01);
//        assertEquals("RUB", request.getCurrency());
//        assertEquals(LocalDate.of(2025, 4, 5), request.getDate());
//    }
}

