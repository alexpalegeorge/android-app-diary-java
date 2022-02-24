package com.example.paindiary.repository;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.paindiary.RetrofitClient;
import com.example.paindiary.RetrofitInterface;
import com.example.paindiary.dao.PainRecordDAO;
import com.example.paindiary.database.PainRecordDatabase;
import com.example.paindiary.entity.PainRecord;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PainDiaryRepository {
    private PainRecordDAO painRecordDAO;
    private LiveData<List<PainRecord>> allPainRecords;
    private RetrofitInterface retrofitInterface;
    private LatLng latLng;
    private static String APPID = "c28e0695e2c30ac14547ac5338146037";
    private static int temperature;
    private static int humidity;
    private static int pressure;
    //private List<Integer> weather;
    private MutableLiveData<List<Integer>> currentWeather;

    public PainDiaryRepository(Application application) {
        // Connect to the database, attach the DAO interface, and copy all data into this repository
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        painRecordDAO = db.painRecordDAO();
        allPainRecords = painRecordDAO.getAll();
        // Create the Retrofit Interface and use it to get the current weather, saving it into this
        // repository
        retrofitInterface = RetrofitClient.getRetrofitService();
        latLng = new LatLng(-37.876823, 145.045837); // hard codes weather location to Monash)
        fetchWeatherData();
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public LiveData<List<PainRecord>> getAllByLocation(final String painRecordLocation) {
        return painRecordDAO.getAllByLocation(painRecordLocation);
    }


    public void insert(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.insert(painRecord);
            }
        });
    }



    public void deleteAll() {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.deleteAll();
            }
        });
    }

    public void delete(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.delete(painRecord);
            }
        });
    }

    public void updatePainRecord(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDAO.update(painRecord);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIdFuture(final int painRecordId) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painRecordDAO.findById(painRecordId);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final String painRecordDate) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painRecordDAO.findByDate(painRecordDate);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }



    private CompletableFuture<List<Integer>> fetchWeatherData() {

        Callback<WeatherResponse> responseCallback = new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    temperature = Math.round(weatherResponse.main.temp);
                    humidity = Math.round(weatherResponse.main.humidity);
                    pressure = Math.round(weatherResponse.main.pressure);
                    List<Integer> weather = Arrays.asList(temperature, humidity, pressure);
                    currentWeather.postValue(weather);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
            }
        };

        return CompletableFuture.supplyAsync(new Supplier<List<Integer>>() {
            @Override
            public List<Integer> get() {
                getCallbackData(responseCallback);
                return Arrays.asList(temperature, humidity, pressure);
            }
        });
    }

    public void getCallbackData(Callback<WeatherResponse> callback) {
        String lat = String.valueOf(latLng.getLatitude());
        String lng = String.valueOf(latLng.getLongitude());
        retrofitInterface.getWeather(lat, lng, APPID, "metric").enqueue(callback);
    }
    public MutableLiveData<List<Integer>> getCurrentWeather() {
        if (currentWeather == null) {
            currentWeather = new MutableLiveData<List<Integer>>();
            currentWeather.setValue(Arrays.asList(temperature, humidity, pressure));
        }
        return currentWeather;
    }

}
