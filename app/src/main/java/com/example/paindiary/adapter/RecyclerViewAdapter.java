package com.example.paindiary.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiary.databinding.RvLayoutBinding;
import com.example.paindiary.entity.PainRecord;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter
        <RecyclerViewAdapter.ViewHolder> {

    private LiveData<List<PainRecord>> painRecordList;

    public RecyclerViewAdapter(LiveData<List<PainRecord>> painRecordList) {
        this.painRecordList = painRecordList;
    }

    //This method creates a new view holder that is constructed with a new View,
    //inflated from a layout
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        RvLayoutBinding binding =
                RvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);
        return new ViewHolder(binding);
    }
    // this method binds the view holder created with data that will be displayed
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        final PainRecord painRecord = painRecordList.getValue().get(position);

        //add bindings for a recycler view row
        String set_date = "Entry: " + painRecord.date;
        viewHolder.binding.date.setText(set_date);

        String set_intensity = "Pain Intensity: " + painRecord.intensityLevel;
        viewHolder.binding.intensity.setText(set_intensity);

        String set_location = "Pain Location: " + painRecord.location;
        viewHolder.binding.location.setText(set_location);

        String set_mood = "Mood Level: " + painRecord.moodLevel;
        viewHolder.binding.mood.setText(set_mood);

        String set_steps_goal = "Steps Goal: " + painRecord.stepsGoal;
        viewHolder.binding.stepsGoal.setText(set_steps_goal);

        String set_steps_actual = "Steps Taken: " + painRecord.stepsTaken;
        viewHolder.binding.stepsActual.setText(set_steps_actual);

        String set_temperature = "Temperature: " + painRecord.temperature;
        viewHolder.binding.temperature.setText(set_temperature);

        String set_humidity = "Humidity: " + painRecord.humidity;
        viewHolder.binding.humidity.setText(set_humidity);

        String set_pressure = "Air pressure: " + painRecord.pressure;
        viewHolder.binding.pressure.setText(set_pressure);
    }

    @Override
    public int getItemCount() {
        return painRecordList.getValue().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RvLayoutBinding binding;
        public ViewHolder(RvLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
