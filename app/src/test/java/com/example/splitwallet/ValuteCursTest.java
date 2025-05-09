package com.example.splitwallet;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.splitwallet.models.ValuteCurs;

public class ValuteCursTest {

    @Test
    public void testGetConvertedRate() {
        ValuteCurs valute = new ValuteCurs();
        valute.setNominal(10);
        valute.setRate(75.0);

        double expected = 7.5;
        assertEquals(expected, valute.getConvertedRate(), 0.0001);
    }

    @Test
    public void testSetAndGetFields() {
        ValuteCurs valute = new ValuteCurs();
        valute.setName("USD");
        valute.setCode("USD");
        valute.setNominal(1);
        valute.setRate(74.5);

        assertEquals("USD", valute.getName());
        assertEquals("USD", valute.getCode());
        assertEquals(1, valute.getNominal(), 0.001);
        assertEquals(74.5, valute.getRate(), 0.001);
    }
}

