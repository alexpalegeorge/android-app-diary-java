package com.example.paindiary;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.paindiary.entity.PainRecord;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadWorker extends Worker {

    private String DATABASE_URL = "https://paindiary31209351-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private DatabaseReference mDatabase;
    private PainRecord painRecord;

    public UploadWorker(@NonNull Context context,
                        @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Get all the values from the Work Manager and create a Pain Record
        int uid = getInputData().getInt("uid", 0);
        int intensityLevel = getInputData().getInt("intensity_level", 0);
        String location = getInputData().getString("location");
        String moodLevel = getInputData().getString("mood_level");
        int stepsGoal = getInputData().getInt("steps_goal", 10000);
        int stepsTaken = getInputData().getInt("steps_taken", 0);
        String date = getInputData().getString("date");
        int temperature = getInputData().getInt("temperature", 0);
        int humidity = getInputData().getInt("humidity", 0);
        int pressure = getInputData().getInt("pressure", 0);
        String email = getInputData().getString("email");

        painRecord = new PainRecord(
                intensityLevel,
                location,
                moodLevel,
                stepsGoal,
                stepsTaken,
                date,
                temperature,
                humidity,
                pressure,
                email);
        painRecord.uid = uid;

        // Do the work here--in this case, upload the pain record to firebase.
        updateDatabase(painRecord);

        return Result.success();
    }

    private Task updateDatabase(PainRecord painRecord) {
        String userSafeCharacters = painRecord.email.replace(".", "-");
        mDatabase = FirebaseDatabase.getInstance(DATABASE_URL).getReference();
        Task task = mDatabase.child(userSafeCharacters).child(painRecord.date).setValue(painRecord);
        return task;
    }
}
