package com.example.splitwallet.api;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CbrApiService {
    @POST("/DailyInfoWebServ/DailyInfo.asmx")
    @Headers({
            "Content-Type: text/xml; charset=utf-8",
            "SOAPAction: http://web.cbr.ru/GetCursOnDate"
    })
    Call<String> getRatesOnDate(@Body RequestBody body);
}