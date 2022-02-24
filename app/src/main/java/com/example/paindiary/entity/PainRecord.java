package com.example.paindiary.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class PainRecord {
    @PrimaryKey (autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "intensity_level")
    @NonNull
    public int intensityLevel;

    @ColumnInfo(name = "location")
    @NonNull
    public String location;

    @ColumnInfo(name = "mood_level")
    @NonNull
    public String moodLevel;

    @ColumnInfo(name = "steps_goal")
    @NonNull
    public int stepsGoal;

    @ColumnInfo(name = "steps_taken")
    @NonNull
    public int stepsTaken;

    @ColumnInfo(name = "date")
    @NonNull
    public String date;

    @ColumnInfo(name = "temperature")
    @NonNull
    public int temperature;

    @ColumnInfo(name = "humidity")
    @NonNull
    public int humidity;

    @ColumnInfo(name = "pressure")
    @NonNull
    public int pressure;

    public String email;

    public PainRecord (int intensityLevel,
                       @NonNull String location,
                       @NonNull String moodLevel,
                       int stepsGoal,
                       int stepsTaken,
                       @NonNull String date,
                       int temperature,
                       int humidity,
                       int pressure,
                       @NonNull String email) {
        this.intensityLevel = intensityLevel;
        this.location = location;
        this.moodLevel = moodLevel;
        this.stepsGoal = stepsGoal;
        this.stepsTaken = stepsTaken;
        this.date = date;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.email = email;
    }
}
