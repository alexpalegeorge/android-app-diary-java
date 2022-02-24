package com.example.paindiary.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.paindiary.MainActivity;
import com.example.paindiary.UploadWorker;
import com.example.paindiary.databinding.FragmentEntryBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainDiaryViewModel;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FragmentEntry extends Fragment {
    private FragmentEntryBinding entryBinding;
    private PainDiaryViewModel painDiaryViewModel;

    private int enteredIntensity;
    private String enteredLocation;
    private String enteredMood;
    private int enteredStepsGoal;
    private int enteredStepsActual;

    private String currentDate;
    private int currentTemperature;
    private int currentHumidity;
    private int currentPressure;
    private String email;

    private final int UPLOAD_MINUTE = 22 * 60;

    public FragmentEntry(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        entryBinding = FragmentEntryBinding.inflate(inflater, container, false);
        View view = entryBinding.getRoot();

        painDiaryViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainDiaryViewModel.class);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        currentDate = df.format(date);

        // sets the weather local variables when weather livedata is changed
        final Observer<List<Integer>> weatherObserver = new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> integers) {
                currentTemperature = integers.get(0);
                currentHumidity = integers.get(1);
                currentPressure = integers.get(2);
            }
        };
        painDiaryViewModel.getCurrentWeather().observe(getActivity(), weatherObserver);

        try {
            email = ((MainActivity) getActivity()).getUserEmail();
        } catch (NullPointerException e) {
            email = "not provided";
            Toast.makeText(getContext(), "no email available", Toast.LENGTH_LONG).show();
        }

        // Set the pain intensity label and update it when it is moved
        SeekBar seekBar = entryBinding.seekBarIntensity;
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        updateIntensity();

        // Disable save button if entry exists, and set fields to previously input values
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CompletableFuture<PainRecord> painRecordCompletableFuture = painDiaryViewModel
                    .findByDateFuture(currentDate);
            painRecordCompletableFuture.thenApply(painRecord -> {
                if (painRecord != null) {
                    // Disable save button, and prompt user
                    buttonToggle("edit");
                    setFieldValues(painRecord);
                    Toast.makeText(getContext(),
                            "Currently displaying today's entry.",
                            Toast.LENGTH_LONG).show();
                } else {
                    // Disable update button
                    buttonToggle("save");
                }
                return painRecord;
            });
        }

        // Fetch input from view and APIs then add entry to database when the save button is pressed
        entryBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (fetchFieldValues()) {
                    //save to room db via the view model
                    PainRecord painRecord = new PainRecord(
                            enteredIntensity,
                            enteredLocation,
                            enteredMood,
                            enteredStepsGoal,
                            enteredStepsActual,
                            currentDate,
                            currentTemperature,
                            currentHumidity,
                            currentPressure,
                            email);
                    painDiaryViewModel.insert(painRecord);
                    enqueRecordForUpload(painRecord);
                    buttonToggle("edit");
                    Toast.makeText(getContext(),
                            "Successfully added today's entry.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(),
                            "Please fill every field before saving.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        entryBinding.buttonSaveDisabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),
                        "You have already created an entry for today. You can still use the " +
                                "'Edit' button to update today's entry.",
                        Toast.LENGTH_LONG).show();
            }
        });
        // Add edit button functionality
        entryBinding.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fetchFieldValues()) {
                    //update entry in room db via the view model
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        CompletableFuture<PainRecord> painRecordCompletableFuture = painDiaryViewModel
                                .findByDateFuture(currentDate);
                        try {
                            PainRecord painRecord = painRecordCompletableFuture.get();
                            painRecord.intensityLevel = enteredIntensity;
                            painRecord.location = enteredLocation;
                            painRecord.moodLevel = enteredMood;
                            painRecord.stepsGoal = enteredStepsGoal;
                            painRecord.stepsTaken = enteredStepsActual;
                            Toast.makeText(getContext(),
                                    "Successfully updated today's entry.",
                                    Toast.LENGTH_LONG).show();
                            painDiaryViewModel.update(painRecord);
                            enqueRecordForUpload(painRecord);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        /*
                        painRecordCompletableFuture.thenApply(painRecord -> {
                            if (painRecord != null) {
                                painRecord.intensityLevel = enteredIntensity;
                                painRecord.location = enteredLocation;
                                painRecord.moodLevel = enteredMood;
                                painRecord.stepsGoal = enteredStepsGoal;
                                painRecord.stepsTaken = enteredStepsActual;
                                Toast.makeText(getContext(),
                                        "Successfully updated today's entry.",
                                        Toast.LENGTH_LONG).show();
                                painDiaryViewModel.update(painRecord);
                                enqueRecordForUpload(painRecord);
                            } else {
                                Toast.makeText(getContext(),
                                        "Error: Could not find today's entry.",
                                        Toast.LENGTH_LONG).show();
                            }
                            return painRecord;
                        });

                         */
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Please fill every field before editing.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        entryBinding.buttonEditDisabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),
                        "You have not yet saved an entry for today. Complete the form and " +
                                "press the 'Save' button to create today's entry.",
                        Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    // The pain intensity seekBar listener
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateIntensity();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    public void buttonToggle(String focusButton) {
        if (focusButton.equals("edit")) {
            entryBinding.buttonSaveDisabled.setVisibility(View.VISIBLE);
            entryBinding.buttonSave.setVisibility(View.GONE);
            entryBinding.buttonEdit.setVisibility(View.VISIBLE);
            entryBinding.buttonEditDisabled.setVisibility(View.GONE);
        }
        if (focusButton.equals("save")) {
            entryBinding.buttonSave.setVisibility(View.VISIBLE);
            entryBinding.buttonSaveDisabled.setVisibility(View.GONE);
            entryBinding.buttonEditDisabled.setVisibility(View.VISIBLE);
            entryBinding.buttonEdit.setVisibility(View.GONE);
        }
    }

    private boolean fetchFieldValues() {
        boolean flag = true;
        enteredIntensity = entryBinding.seekBarIntensity.getProgress();

        int checkedLocationId = entryBinding.radioGroupLocation
                .getCheckedRadioButtonId();
        int checkedMoodId = entryBinding.radioGroupMood
                .getCheckedRadioButtonId();
        String stepsGoalText = entryBinding.editTextNumberStepsGoal.getText().toString();
        String stepsActualText = entryBinding.editTextNumberStepsActual.getText().toString();

        if (validateEntry(checkedLocationId)
                && validateEntry(checkedMoodId)
                && validateEntry(stepsGoalText)
                && validateEntry(stepsActualText)) {
            enteredLocation = entryBinding.radioGroupLocation
                    .findViewById(checkedLocationId).getContentDescription().toString();
            enteredMood = entryBinding.radioGroupMood
                    .findViewById(checkedMoodId).getContentDescription().toString();
            enteredStepsGoal = Integer.parseInt(stepsGoalText);
            enteredStepsActual = Integer.parseInt(stepsActualText);
        } else {
            flag = false;
            Toast.makeText(getContext(),
                    "Please ensure all data is entered before saving.",
                    Toast.LENGTH_LONG).show();
        }
        return flag;
    }
    public void setFieldValues(PainRecord painRecord) {
        entryBinding.seekBarIntensity.setProgress(painRecord.intensityLevel);
        switch (painRecord.location) {
            case "back":
                entryBinding.radioBack.setChecked(true);
                break;
            case "neck":
                entryBinding.radioNeck.setChecked(true);
                break;
            case "head":
                entryBinding.radioHead.setChecked(true);
                break;
            case "knees":
                entryBinding.radioKnees.setChecked(true);
                break;
            case "hips":
                entryBinding.radioHips.setChecked(true);
                break;
            case "abdomen":
                entryBinding.radioAbdomen.setChecked(true);
                break;
            case "elbows":
                entryBinding.radioElbows.setChecked(true);
                break;
            case "shoulders":
                entryBinding.radioShoulders.setChecked(true);
                break;
            case "shins":
                entryBinding.radioShins.setChecked(true);
                break;
            case "jaw":
                entryBinding.radioJaw.setChecked(true);
                break;
            case "facial":
                entryBinding.radioFacial.setChecked(true);
                break;
        }
        switch (painRecord.moodLevel) {
            case "very low":
                entryBinding.radioVeryLow.setChecked(true);
                break;
            case "low":
                entryBinding.radioLow.setChecked(true);
                break;
            case "average":
                entryBinding.radioAverage.setChecked(true);
                break;
            case "good":
                entryBinding.radioGood.setChecked(true);
                break;
            case "very good":
                entryBinding.radioVeryGood.setChecked(true);
                break;
        }
        entryBinding.editTextNumberStepsGoal.setText(String.valueOf(painRecord.stepsGoal));
        entryBinding.editTextNumberStepsActual.setText(String.valueOf(painRecord.stepsTaken));
    }

    public boolean validateEntry(String entry) {
        boolean flag = true;
        if ((entry.isEmpty() || entry == null)) { flag = false; }
        return flag;
    }

    public boolean validateEntry(int entry) {
        boolean flag = true;
        if (entry == 0) { flag = false; }
        return flag;
    }

    // Sets the pain intensity seekbar label to initially reflect its current position
    public void updateIntensity() {
        int enteredIntensity = entryBinding.seekBarIntensity.getProgress();
        String intensityLabel = "Intensity level: " + enteredIntensity;
        entryBinding.intensityLabelProgress.setText(intensityLabel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        entryBinding = null;
    }

    private void enqueRecordForUpload(PainRecord painRecord) {
        Data recordData = new Data.Builder()
                .putInt("uid", painRecord.uid)
                .putInt("intensity_level", painRecord.intensityLevel)
                .putString("location", painRecord.location)
                .putString("mood_level", painRecord.moodLevel)
                .putInt("steps_goal", painRecord.stepsGoal)
                .putInt("steps_taken", painRecord.stepsTaken)
                .putString("date", painRecord.date)
                .putInt("temperature", painRecord.temperature)
                .putInt("humidity", painRecord.humidity)
                .putInt("pressure", painRecord.pressure)
                .putString("email", painRecord.email)
                .build();

        long delay = 0;

        if (DateTime.now().getMinuteOfDay() < UPLOAD_MINUTE) {
            delay = new Duration(DateTime.now() , DateTime.now().withTimeAtStartOfDay()
                    .plusMinutes(UPLOAD_MINUTE)).getStandardMinutes();
        } else {
            delay = new Duration(DateTime.now() , DateTime.now().withTimeAtStartOfDay()
                    .plusDays(1).plusHours(UPLOAD_MINUTE)).getStandardMinutes();
        }

        OneTimeWorkRequest uploadRecordWork = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setInputData(recordData)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(getContext())
                .beginUniqueWork(painRecord.date, ExistingWorkPolicy.REPLACE, uploadRecordWork)
                .enqueue();
    }

}