package com.example.splitwallet;

import com.example.splitwallet.api.CbrApiService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.RequestBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static org.junit.Assert.*;

public class CbrApiServiceTest {

    private MockWebServer mockWebServer;
    private CbrApiService cbrApiService;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        cbrApiService = retrofit.create(CbrApiService.class);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void getRatesOnDate_success() throws Exception {
        String xmlResponse = "<GetCursOnDateResult><ValuteCursOnDate>...</ValuteCursOnDate></GetCursOnDateResult>";
        mockWebServer.enqueue(new MockResponse().setBody(xmlResponse).setResponseCode(200));

        RequestBody body = RequestBody.create(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<soap:Envelope ...>...</soap:Envelope>",
                okhttp3.MediaType.parse("text/xml")
        );

        Call<String> call = cbrApiService.getRatesOnDate(body);
        Response<String> response = call.execute();

        assertTrue(response.isSuccessful());
        assertTrue(response.body().contains("ValuteCursOnDate"));
    }
}
