package com.example.fithealth;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrackWorkoutFragment extends Fragment {
    private Spinner exerciseSpinner;
    private EditText editTextGoalHours;
    private EditText editTextGoalMinutes;
    private EditText editTextGoalSeconds;
    private Chronometer chronometer;
    private long pausedTime = 0;
    private int durationHours;
    private int durationMinutes;
    private int durationSeconds;

    private String selectedDate; // Store the selected date
    private boolean isTimerRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_workout, container, false);

        exerciseSpinner = rootView.findViewById(R.id.spinnerExercise);
        chronometer = rootView.findViewById(R.id.chronometer);
        editTextGoalHours = rootView.findViewById(R.id.editTextGoalHours);
        editTextGoalMinutes = rootView.findViewById(R.id.editTextGoalMinutes);
        editTextGoalSeconds = rootView.findViewById(R.id.editTextGoalSeconds);

        // Initialize exercise spinner
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getExerciseList());
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);

        // Set initial date to today's date
        selectedDate = getCurrentDate();

        Button btnStartStopTimer = rootView.findViewById(R.id.btnStartStopTimer);
        btnStartStopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopTimer(v);
            }
        });

        // Find the "Save Workout" button
        Button btnSaveWorkout = rootView.findViewById(R.id.btnSaveWorkout);

        // Set OnClickListener for the "Save Workout" button
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        return rootView;
    }

    private ArrayList<String> getExerciseList() {
        return new ArrayList<>(Arrays.asList("Walking", "Running", "Cycling", "Weight lifting", "Body exercises"));
    }

    // Method to show the DatePickerDialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle the selected date
                        selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        // Set the selected date wherever needed
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // Method to start or stop the timer
    public void startStopTimer(View view) {
        if (isTimerRunning) {
            // Stop the timer and calculate the paused time
            chronometer.stop();
            isTimerRunning = false;
            pausedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        } else {
            // Start or resume the timer
            if (chronometer.getBase() == 0) {
                // If the timer hasn't been started yet, set the base time to the current time minus paused time
                chronometer.setBase(SystemClock.elapsedRealtime() - pausedTime);
            } else {
                // If the timer has been stopped, adjust the base time using the paused time
                chronometer.setBase(SystemClock.elapsedRealtime() - pausedTime);
            }

            // Set the format to display hours, minutes, and seconds
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer cArg) {
                    long time = SystemClock.elapsedRealtime() - cArg.getBase();
                    int h = (int) (time / 3600000);
                    int m = (int) (time - h * 3600000) / 60000;
                    int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                    String hh = h < 10 ? "0" + h : String.valueOf(h);
                    String mm = m < 10 ? "0" + m : String.valueOf(m);
                    String ss = s < 10 ? "0" + s : String.valueOf(s);
                    cArg.setText(hh + ":" + mm + ":" + ss);
                }
            });

            chronometer.start();
            isTimerRunning = true;
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    private void saveWorkout() {
        // Get selected exercise and goal duration
        String selectedExercise = exerciseSpinner.getSelectedItem().toString();
        String goalHours = editTextGoalHours.getText().toString();
        String goalMinutes = editTextGoalMinutes.getText().toString();
        String goalSeconds = editTextGoalSeconds.getText().toString();

        // Validate input for exercise
        if (TextUtils.isEmpty(selectedExercise)) {
            Toast.makeText(requireContext(), "Please select an exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input for goal duration
        if (TextUtils.isEmpty(goalHours) || TextUtils.isEmpty(goalMinutes) || TextUtils.isEmpty(goalSeconds)) {
            Toast.makeText(requireContext(), "Please enter a goal duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse goal duration
        int goalHoursInt = Integer.parseInt(goalHours);
        int goalMinutesInt = Integer.parseInt(goalMinutes);
        int goalSecondsInt = Integer.parseInt(goalSeconds);

        // Get the duration from the stopped timer
        String duration = getStoppedTimerDuration();

        // Parse duration to extract hours, minutes, and seconds
        String[] durationParts = duration.split(":");
        if (durationParts.length == 3) {
            durationHours = Integer.parseInt(durationParts[0]);
            durationMinutes = Integer.parseInt(durationParts[1]);
            durationSeconds = Integer.parseInt(durationParts[2]);
        } else {
            // Handle invalid input
            Toast.makeText(requireContext(), "Invalid duration format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new instance of WorkoutEntity with user-entered details and the selected date
        WorkoutEntity workoutEntity = new WorkoutEntity(
                selectedExercise,
                durationHours,
                durationMinutes,
                durationSeconds,
                goalHoursInt,
                goalMinutesInt,
                goalSecondsInt,
                selectedDate
        );

        // Save the workout to the database asynchronously using AsyncTask
        new SaveWorkoutAsyncTask().execute(workoutEntity);

        // Display a confirmation message with goal duration and performance message
        String toastMessage = "Workout Saved:\n" + workoutEntity.toString();

        // Compare actual duration with goal duration
        if (compareDurationWithGoal(workoutEntity.getDurationInSeconds(), workoutEntity)) {
            toastMessage += "\nYou achieved your goal!";
        } else {
            toastMessage += "\nKeep pushing towards your goal!";
        }

        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();

        // Navigate back to the Home fragment
        navigateToWorkoutHistoryFragment();
    }

    // Helper method to compare actual duration with goal duration
    private boolean compareDurationWithGoal(int actualDurationSeconds, WorkoutEntity workoutEntity) {
        // Compare the two durations
        return actualDurationSeconds >= workoutEntity.getGoalInSeconds();
    }

    // Helper method to get the duration from the stopped timer as a formatted string
    private String getStoppedTimerDuration() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;

        // Return the formatted duration
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Helper method to navigate to the Home fragment
    private void navigateToWorkoutHistoryFragment() {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new WorkoutHistoryFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private static class SaveWorkoutAsyncTask extends AsyncTask<WorkoutEntity, Void, Void> {
        @Override
        protected Void doInBackground(WorkoutEntity... workoutEntities) {
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().insert(workoutEntities[0]);
            return null;
        }
    }
}