package com.example.splitwallet.models;

import android.os.Build;
import android.util.Log;

import com.example.splitwallet.api.CbrApiService;
import com.example.splitwallet.api.RetrofitClient;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class CurrencyConverter {
    public interface ConversionCallback {
        void onSuccess(CreateExpenseRequest rubRequest);
        void onError(String error);
    }

    private static final Map<String, String> CURRENCY_CODES = Map.of(
            "USD", "R01235",
            "EUR", "R01239",
            "GBP", "R01035"
    );
    private static final List<String> SUPPORTED_CURRENCIES = List.of(
            "USD", "EUR", "GBP"
    );

    public void convertToRub(Long groupId, CreateExpenseRequest request,
                             String token, ConversionCallback callback) {
        if (!SUPPORTED_CURRENCIES.contains(request.getCurrency())) {
            callback.onError("Валюта " + request.getCurrency() + " не поддерживается");
            return;
        }
        String currencyCode = CURRENCY_CODES.get(request.getCurrency());

        if (currencyCode == null) {
            callback.onError("Валюта не поддерживается");
            return;
        }

        String soapRequest = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:web=\"http://web.cbr.ru/\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <web:GetCursOnDate>\n"
                + "         <web:On_date>" + LocalDate.now() + "</web:On_date>\n"
                + "      </web:GetCursOnDate>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";

        RequestBody body = RequestBody.create(
                soapRequest,
                MediaType.parse("text/xml; charset=utf-8")
        );

        Retrofit retrofit = RetrofitClient.getCbrRetrofitInstance();

        retrofit.create(CbrApiService.class)
                .getRatesOnDate(body)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            String xmlResponse = response.body();
                            Log.d("XML_RESPONSE", xmlResponse);
                            processCbrResponse(xmlResponse, currencyCode, request, callback);
                        } else {
                            callback.onError("Ошибка сервера: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        callback.onError("Ошибка сети: " + t.getMessage());
                    }
                });
    }

    private List<ValuteCurs> parseResponse(String xml) throws XmlPullParserException, IOException {
        List<ValuteCurs> valutes = new ArrayList<>();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xml));

        int eventType = parser.getEventType();
        ValuteCurs currentValute = null;
        boolean inValuteCursOnDate = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("ValuteCursOnDate".equals(tagName)) {
                        currentValute = new ValuteCurs();
                        inValuteCursOnDate = true;
                    } else if (inValuteCursOnDate && currentValute != null) {
                        switch (tagName) {
                            case "Vname":
                                currentValute.setName(parser.nextText().trim());
                                break;
                            case "Vnom":
                                currentValute.setNominal(Double.parseDouble(parser.nextText()));
                                break;
                            case "Vcurs":
                                currentValute.setRate(Double.parseDouble(parser.nextText()));
                                break;
                            case "VchCode":
                                currentValute.setCode(parser.nextText().trim());
                                break;
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("ValuteCursOnDate".equals(tagName) && currentValute != null) {
                        valutes.add(currentValute);
                        currentValute = null;
                        inValuteCursOnDate = false;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return valutes;
    }

    private void processCbrResponse(String xmlResponse, String currencyCode,
                                    CreateExpenseRequest request,
                                    ConversionCallback callback) {
        try {
            List<ValuteCurs> valutes = parseResponse(xmlResponse);

            if (valutes == null || valutes.isEmpty()) {
                callback.onError("Нет данных о курсах валют");
                return;
            }

            // Ищем валюту по международному коду
            for (ValuteCurs valute : valutes) {
                if (request.getCurrency().equals(valute.getCode())) {
                    double convertedAmount = request.getAmount() * valute.getConvertedRate();
                    CreateExpenseRequest rubRequest = createRubRequest(request, convertedAmount);
                    callback.onSuccess(rubRequest);
                    return;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                callback.onError("Валюта " + request.getCurrency() + " не найдена в ответе ЦБ. Доступные валюты: " +
                        valutes.stream().map(ValuteCurs::getCode).toList());
            }
        } catch (Exception e) {
            Log.e("CBR_ERROR", "Ошибка обработки данных", e);
            callback.onError("Ошибка обработки данных: " + e.getMessage());
        }
    }

    private CreateExpenseRequest createRubRequest(CreateExpenseRequest original, double convertedAmount) {
        return new CreateExpenseRequest(
                original.getName(),
                original.getDate(),
                original.getDescription(),
                convertedAmount,
                "RUB"
        );
    }
}