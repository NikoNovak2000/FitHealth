package com.example.fithealth;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class SavePreviousWorkoutFragment extends Fragment {
    private Spinner exerciseSpinner;
    private EditText editTextDuration;
    private EditText editTextGoal;
    private Button btnSelectDate;

    private String selectedDate; // Store the selected date

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_previous_workout, container, false);

        exerciseSpinner = rootView.findViewById(R.id.spinnerExercise);
        editTextDuration = rootView.findViewById(R.id.editTextDuration);
        editTextGoal = rootView.findViewById(R.id.editTextGoal);
        btnSelectDate = rootView.findViewById(R.id.btnSelectDate);

        // Initialize exercise spinner
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getExerciseList());
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);

        // Set click listener for the save button
        Button btnSaveWorkout = rootView.findViewById(R.id.btnSaveWorkout);
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        // Set click listener for selecting date
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // Set initial date to today's date
        selectedDate = getCurrentDate();
        btnSelectDate.setText(selectedDate);

        return rootView;
    }

    private ArrayList<String> getExerciseList() {
        return new ArrayList<>(Arrays.asList("Walking", "Running", "Cycling", "Weight lifting", "Body exercises"));
    }

    private void saveWorkout() {
        // Get selected exercise and workout details
        String selectedExercise = exerciseSpinner.getSelectedItem().toString();
        String duration = editTextDuration.getText().toString();
        String goal = editTextGoal.getText().toString();

        // Validate input for exercise
        if (TextUtils.isEmpty(selectedExercise) || TextUtils.isEmpty(duration)) {
            Toast.makeText(requireContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input for goal exercise
        if (TextUtils.isEmpty(goal)) {
            Toast.makeText(requireContext(), "Please enter a goal", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new instance of WorkoutEntity with user-entered details and the selected date
        WorkoutEntity workoutEntity = new WorkoutEntity(selectedExercise, duration, goal, selectedDate);

        // Save the workout to the database asynchronously using AsyncTask
        new SaveWorkoutAsyncTask().execute(workoutEntity);

        // Display a confirmation message with goal duration and performance message
        String toastMessage = "Workout Saved:\n" + workoutEntity.toString();

        // Compare actual duration with goal duration
        if (compareDurationWithGoal(duration, goal)) {
            toastMessage += "\nYou are doing a great job!";
        } else {
            toastMessage += "\nKeep on pushing!";
        }

        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    // Helper method to compare actual duration with goal duration
    private boolean compareDurationWithGoal(String actualDuration, String goalDuration) {
        try {
            // Convert duration strings to integers for comparison
            int actual = Integer.parseInt(actualDuration);
            int goal = Integer.parseInt(goalDuration);

            // Compare the two durations
            return actual >= goal;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false; // Handle invalid input gracefully
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());
        Log.d("DateDebug", "Current Date: " + currentDate);
        return currentDate;
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
                        btnSelectDate.setText(selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private static class SaveWorkoutAsyncTask extends AsyncTask<WorkoutEntity, Void, Void> {
        @Override
        protected Void doInBackground(WorkoutEntity... workoutEntities) {
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().insert(workoutEntities[0]);
            return null;
        }
    }
}