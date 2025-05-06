package com.example.splitwallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.splitwallet.models.CbrResponse;
import com.example.splitwallet.models.ValuteCurs;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.util.List;

public class CbrResponseTest {

    @Test
    public void testGetValutes_returnsCorrectData() throws Exception {
        String xml = "<Envelope><Body><GetCursOnDateResponse>" +
                "<GetCursOnDateResult><diffgram>" +
                "<ValuteData><ValuteCursOnDate>" +
                "<Vname>Dollar</Vname><Vnom>1</Vnom>" +
                "<Vcurs>90.50</Vcurs><VchCode>USD</VchCode>" +
                "</ValuteCursOnDate></ValuteData>" +
                "</diffgram></GetCursOnDateResult>" +
                "</GetCursOnDateResponse></Body></Envelope>";

        Serializer serializer = new Persister();
        CbrResponse response = serializer.read(CbrResponse.class, xml);
        List<ValuteCurs> valutes = response.getValutes();

        assertNotNull(valutes);
        assertEquals(1, valutes.size());
        assertEquals("USD", valutes.get(0).getCode());
    }
}

