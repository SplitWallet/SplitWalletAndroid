package com.example.splitwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.api.RetrofitClient;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class CurrencyConverterTest {

    private MockWebServer server;
    private CurrencyConverter converter;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        RetrofitClient.overrideBaseUrl(server.url("/").toString());
        converter = new CurrencyConverter();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testConvertToRub_validCurrency_successCallback() {
        String mockXml = "<?xml version=\"1.0\"?><Envelope><Body>" +
                "<GetCursOnDateResponse><GetCursOnDateResult>" +
                "<diffgram><ValuteData><ValuteCursOnDate>" +
                "<Vname>Dollar</Vname><Vnom>1</Vnom><Vcurs>90.0</Vcurs>" +
                "<VchCode>USD</VchCode></ValuteCursOnDate>" +
                "</ValuteData></diffgram></GetCursOnDateResult>" +
                "</GetCursOnDateResponse></Body></Envelope>";

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(mockXml));

        CreateExpenseRequest request = new CreateExpenseRequest("Lunch", LocalDate.now(), "Food", 10.0, "USD");

        converter.convertToRub(1L, request, "", new CurrencyConverter.ConversionCallback() {
            @Override
            public void onSuccess(CreateExpenseRequest rubRequest) {
                assertEquals("RUB", rubRequest.getCurrency());
                assertEquals(900.0, rubRequest.getAmount(), 0.001);
            }

            @Override
            public void onError(String error) {
                fail("Should not reach error: " + error);
            }
        });
    }
}

