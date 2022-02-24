package com.example.paindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindiary.databinding.FragmentHomeBinding;
import com.example.paindiary.viewmodel.PainDiaryViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FragmentHome extends Fragment {
    private FragmentHomeBinding homeBinding;
    private PainDiaryViewModel painDiaryViewModel;

    public FragmentHome(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = homeBinding.getRoot();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
        homeBinding.textDate.setText(df.format(date));

        painDiaryViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainDiaryViewModel.class);

        // sets the weather local variables when weather livedata is changed
        Observer<List<Integer>> weatherObserver = weather -> {
            String temperatureText = "Temperature: " +
                    weather.get(0) +
                    "\u00B0C";
            homeBinding.weatherTemperature.setText(temperatureText);
            String humidityText = "Humidity: " +
                    weather.get(1) +
                    "%";
            homeBinding.weatherHumidity.setText(humidityText);
            String pressureText = "Air Pressure: " +
                    weather.get(2) +
                    "hPa";
            homeBinding.weatherPressure.setText(pressureText);
        };
        painDiaryViewModel.getCurrentWeather().observe(getActivity(), weatherObserver);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeBinding = null;
    }
}
