package com.example.splitwallet;

import org.junit.Before;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

import com.example.splitwallet.models.CreateExpenseRequest;
import com.example.splitwallet.models.CurrencyConverter;
import com.example.splitwallet.models.ValuteCurs;

public class CurrencyConverterTest {

    private CurrencyConverter converter;
    private CreateExpenseRequest sampleRequest;

    @Before
    public void setup() {
        converter = new CurrencyConverter();
        sampleRequest = new CreateExpenseRequest(
                "Dinner", LocalDate.now(), "Group dinner", 100.0, "USD"
        );
    }

    @Test
    public void testUnsupportedCurrency() {
        CreateExpenseRequest request = new CreateExpenseRequest("Buy", LocalDate.now(), "Buy something", 50.0, "JPY");

        converter.convertToRub(1L, request, "token", new CurrencyConverter.ConversionCallback() {
            @Override
            public void onSuccess(CreateExpenseRequest rubRequest) {
                fail("Should not succeed with unsupported currency");
            }

            @Override
            public void onError(String error) {
                assertTrue(error.contains("не поддерживается"));
            }
        });
    }

    @Test
    public void testParseResponse_withValidXml() throws IOException, XmlPullParserException {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<ValCurs>"
                + "<ValuteCursOnDate>"
                + "<Vname>Доллар США</Vname>"
                + "<Vnom>1</Vnom>"
                + "<Vcurs>75.0</Vcurs>"
                + "<VchCode>USD</VchCode>"
                + "</ValuteCursOnDate>"
                + "</ValCurs>";

        List<ValuteCurs> valutes = converter.parseResponse(xml);

        assertEquals(1, valutes.size());
        ValuteCurs usd = valutes.get(0);
        assertEquals("USD", usd.getCode());
        assertEquals(1.0, usd.getNominal(), 0.01);
        assertEquals(75.0, usd.getRate(), 0.01);
        assertEquals(75.0, usd.getConvertedRate(), 0.01);
    }

    @Test
    public void testProcessCbrResponse_success() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<ValCurs>"
                + "<ValuteCursOnDate>"
                + "<Vname>Евро</Vname>"
                + "<Vnom>1</Vnom>"
                + "<Vcurs>90.0</Vcurs>"
                + "<VchCode>EUR</VchCode>"
                + "</ValuteCursOnDate>"
                + "</ValCurs>";

        CreateExpenseRequest request = new CreateExpenseRequest("Dinner", LocalDate.now(), "Dinner", 100.0, "EUR");

        converter.processCbrResponse(xml, "R01239", request, new CurrencyConverter.ConversionCallback() {
            @Override
            public void onSuccess(CreateExpenseRequest rubRequest) {
                assertEquals("RUB", rubRequest.getCurrency());
                assertEquals(9000.0, rubRequest.getAmount(), 0.01);
            }

            @Override
            public void onError(String error) {
                fail("Expected success, got error: " + error);
            }
        });
    }

    @Test
    public void testProcessCbrResponse_noMatch() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<ValCurs>"
                + "<ValuteCursOnDate>"
                + "<VchCode>GBP</VchCode>"
                + "<Vnom>1</Vnom>"
                + "<Vcurs>100.0</Vcurs>"
                + "</ValuteCursOnDate>"
                + "</ValCurs>";

        CreateExpenseRequest request = new CreateExpenseRequest("Something", LocalDate.now(), "Desc", 100.0, "USD");

        converter.processCbrResponse(xml, "R01235", request, new CurrencyConverter.ConversionCallback() {
            @Override
            public void onSuccess(CreateExpenseRequest rubRequest) {
                fail("Should not succeed with unmatched currency");
            }

            @Override
            public void onError(String error) {
                assertTrue(error.contains("не найдена"));
            }
        });
    }

    @Test
    public void testParseResponse_validXml() throws Exception {
        CurrencyConverter converter = new CurrencyConverter();
        String xml =
                "<Root>"+
                  "<ValuteCursOnDate>"+
                    "<Vname>US Dollar</Vname>"+
                    "<Vnom>1</Vnom>"+
                    "<Vcurs>90.0</Vcurs>"+
                    "<VchCode>USD</VchCode>"+
                  "</ValuteCursOnDate>"+
                "</Root>";

        Method method = CurrencyConverter.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);
        List<ValuteCurs> valutes = (List<ValuteCurs>) method.invoke(converter, xml);

        assertEquals(1, valutes.size());
        ValuteCurs usd = valutes.get(0);
        assertEquals("US Dollar", usd.getName());
        assertEquals(90.0, usd.getRate(), 0.0001);
        assertEquals(1.0, usd.getNominal(), 0.0001);
        assertEquals("USD", usd.getCode());
    }

    @Test
    public void testCreateRubRequestConversion() throws Exception {
        CurrencyConverter converter = new CurrencyConverter();

        CreateExpenseRequest original = new CreateExpenseRequest(
                "Dinner", LocalDate.of(2023, 10, 1), "Birthday", 10.0, "USD");

        Method createRubRequest = CurrencyConverter.class
                .getDeclaredMethod("createRubRequest", CreateExpenseRequest.class, double.class);
        createRubRequest.setAccessible(true);

        CreateExpenseRequest result = (CreateExpenseRequest)
                createRubRequest.invoke(converter, original, 900.0);

        assertEquals("Dinner", result.getName());
        assertEquals("Birthday", result.getDescription());
        assertEquals("RUB", result.getCurrency());
        assertEquals(Double.valueOf(900.0), result.getAmount());

    }
}
