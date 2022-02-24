package com.example.paindiary.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.repository.PainDiaryRepository;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainDiaryViewModel extends AndroidViewModel {
    private PainDiaryRepository prRepository;
    private LiveData<List<PainRecord>> allPainRecords;
    private MutableLiveData<List<Integer>> currentWeather;

    public PainDiaryViewModel(Application application) {
        super(application);
        prRepository = new PainDiaryRepository(application);
        allPainRecords = prRepository.getAllPainRecords();
        currentWeather = prRepository.getCurrentWeather();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIdFuture(final int painRecordId) {
        return prRepository.findByIdFuture(painRecordId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final String painRecordDate) {
        return prRepository.findByDateFuture(painRecordDate);
    }

    public MutableLiveData<List<Integer>> getCurrentWeather() {
        return currentWeather;
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public LiveData<List<PainRecord>> getAllByLocation(String painLocation) {
        return prRepository.getAllByLocation(painLocation);
    }

    public void insert(PainRecord painRecord) {
        prRepository.insert(painRecord);
    }

    public void deleteAll() {
        prRepository.deleteAll();
    }

    public void update(PainRecord painRecord) {
        prRepository.updatePainRecord(painRecord);
    }
}
