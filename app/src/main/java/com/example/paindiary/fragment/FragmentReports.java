package com.example.paindiary.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.paindiary.adapter.RecyclerViewAdapter;
import com.example.paindiary.databinding.FragmentReportsBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainDiaryViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FragmentReports extends Fragment {
    private FragmentReportsBinding reportsBinding;
    private PieChart pieChart;
    private PieChart donutChart;
    private PainDiaryViewModel painDiaryViewModel;
    private LiveData<List<PainRecord>> painRecordList;
    private String currentDate;

    public FragmentReports(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reportsBinding = FragmentReportsBinding.inflate(inflater, container, false);
        View view = reportsBinding.getRoot();

        painDiaryViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainDiaryViewModel.class);

        // set up Pain Location pie chart with observer
        pieChart = reportsBinding.locationPieChart;
        setupPieChart();
        painDiaryViewModel.getAllPainRecords().observe(getViewLifecycleOwner(),
                new Observer<List<PainRecord>>() {
                    public void onChanged(@Nullable final List<PainRecord> painRecords) {
                        painRecordList = painDiaryViewModel.getAllPainRecords();
                        loadPieChart(painRecordList.getValue());
                    }
                });

        // set up Steps Taken donut chart
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate = df.format(date);
        donutChart = reportsBinding.stepsDonutChart;
        setupDonutChart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> painRecordCompletableFuture = painDiaryViewModel
                    .findByDateFuture(currentDate);
            PainRecord currentPainRecord;
            try {
                currentPainRecord = painRecordCompletableFuture.get();
                if (currentPainRecord != null) {
                    int steps_goal = currentPainRecord.stepsGoal;
                    int steps_taken = currentPainRecord.stepsTaken;
                    loadDonutChart(steps_goal, steps_taken);
                } else {
                    loadDonutChart(10000, 0);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportsBinding = null;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);
    }

    private void setupDonutChart() {
        donutChart.setDrawHoleEnabled(true);
        donutChart.setUsePercentValues(false);
        donutChart.setEntryLabelTextSize(12);
        donutChart.setEntryLabelColor(Color.BLACK);
        donutChart.getDescription().setEnabled(false);
        donutChart.getLegend().setEnabled(false);
        donutChart.setRotationEnabled(false);
    }

    private void loadPieChart(List<PainRecord> painRecords) {
        List<PieEntry> pieEntryList = new ArrayList<>();
        int backCount = 0;
        int neckCount = 0;
        int headCount = 0;
        int kneesCount = 0;
        int hipsCount = 0;
        int abdomenCount = 0;
        int elbowsCount = 0;
        int shouldersCount = 0;
        int shinsCount = 0;
        int jawCount = 0;
        int facialCount = 0;

        for (int i = 0; i < painRecords.size(); i++) {
            switch (painRecords.get(i).location) {
                case "back": backCount++; break;
                case "neck": neckCount++; break;
                case "head": headCount++; break;
                case "knees": kneesCount++; break;
                case "hips": hipsCount++; break;
                case "abdomen": abdomenCount++; break;
                case "elbows": elbowsCount++; break;
                case "shoulders": shouldersCount++; break;
                case "shins": shinsCount++; break;
                case "jaw": jawCount++; break;
                case "facial": facialCount++; break;
            }
        }

        if (neckCount > 0) { pieEntryList.add(new PieEntry(neckCount, "neck")); }
        if (backCount > 0) { pieEntryList.add(new PieEntry(backCount, "back")); }
        if (headCount > 0) { pieEntryList.add(new PieEntry(headCount, "head")); }
        if (kneesCount > 0) { pieEntryList.add(new PieEntry(kneesCount, "knees")); }
        if (hipsCount > 0) { pieEntryList.add(new PieEntry(hipsCount, "hips")); }
        if (abdomenCount > 0) { pieEntryList.add(new PieEntry(abdomenCount, "abdomen")); }
        if (elbowsCount> 0) { pieEntryList.add(new PieEntry(elbowsCount, "elbows")); }
        if (shouldersCount > 0) { pieEntryList.add(new PieEntry(shouldersCount, "shoulders")); }
        if (shinsCount > 0) { pieEntryList.add(new PieEntry(shinsCount, "shins")); }
        if (jawCount > 0) { pieEntryList.add(new PieEntry(jawCount, "jaw")); }
        if (facialCount > 0) { pieEntryList.add(new PieEntry(facialCount, "facial")); }

        PieDataSet dataSet = new PieDataSet(pieEntryList, "Pain Location");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void loadDonutChart(int goal, int steps) {
        List<PieEntry> donutEntryList = new ArrayList<>();

        if (steps > 0) { donutEntryList.add(new PieEntry(steps, "Steps Taken")); }

        int steps_left = goal - steps;
        donutEntryList.add(new PieEntry(steps_left, "Steps to Reach Goal"));

        PieDataSet dataSet = new PieDataSet(donutEntryList, "Steps Taken");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        donutChart.setData(data);
        donutChart.invalidate();
    }
}