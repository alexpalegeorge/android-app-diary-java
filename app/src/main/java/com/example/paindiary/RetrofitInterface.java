package com.example.paindiary;

import com.example.paindiary.repository.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("data/2.5/weather?")
    Call<WeatherResponse> getWeather(@Query("lat") String lat,
                                     @Query("lon") String lon,
                                     @Query("APPID") String app_id,
                                     @Query("units") String metric);
}
