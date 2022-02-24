package com.example.paindiary;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static String weatherBaseUrl = "http://api.openweathermap.org/";
    public static RetrofitInterface getRetrofitService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(weatherBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitInterface.class);
    }
}
