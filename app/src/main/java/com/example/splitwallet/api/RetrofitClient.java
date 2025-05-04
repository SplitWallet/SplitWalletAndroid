package com.example.splitwallet.api;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.splitwallet.models.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@SuppressLint("NewApi")
public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.109.52:8080/"; // 192.168.0.15 win
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Создаем интерцептор для логирования
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message ->
                    Log.d("RETROFIT_HTTP", message)
            );
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Настраиваем OkHttp клиент
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Для даты
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()) // Для даты-времени
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Добавляем наш клиент с логированием
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getCbrRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl("http://www.cbr.ru/")
                .client(new OkHttpClient.Builder()
                        .addInterceptor(chain -> {
                            Request original = chain.request();
                            Request request = original.newBuilder()
                                    .header("Content-Type", "text/xml; charset=utf-8")
                                    .header("SOAPAction", "http://web.cbr.ru/GetCursOnDate")
                                    .method(original.method(), original.body())
                                    .build();
                            return chain.proceed(request);
                        })
                        .build())
                .addConverterFactory(ScalarsConverterFactory.create()) // Для работы с сырым XML
                .build();
    }
}