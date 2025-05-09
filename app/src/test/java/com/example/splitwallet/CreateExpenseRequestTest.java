package com.example.splitwallet;

import org.junit.Test;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

import com.example.splitwallet.models.CreateExpenseRequest;

public class CreateExpenseRequestTest {

    @Test
    public void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        CreateExpenseRequest request = new CreateExpenseRequest(
                "Lunch", date, "Team lunch", 100.0, "USD");

        assertEquals("Lunch", request.getName());
        assertEquals(date, request.getDate());
        assertEquals("Team lunch", request.getDescription());
        assertEquals(Double.valueOf(100.0), request.getAmount());
        assertEquals("USD", request.getCurrency());
    }

}
