package com.example.fithealth;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class SavePreviousWorkoutFragment extends Fragment {
    private Spinner exerciseSpinner;
    private EditText editTextDurationHours;
    private EditText editTextDurationMinutes;
    private EditText editTextDurationSeconds;
    private EditText editTextGoalHours;
    private EditText editTextGoalMinutes;
    private EditText editTextGoalSeconds;
    private Button btnSelectDate;
    private String selectedDate; // To store the selected date
    private TextView totalDistanceTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_previous_workout, container, false);

        // Find references to widget in the layout by its unique identifier
        exerciseSpinner = rootView.findViewById(R.id.spinnerExercise);
        // Initialize exercise spinner
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getExerciseList());
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);
        editTextDurationHours = rootView.findViewById(R.id.editTextDurationHours);
        editTextDurationMinutes = rootView.findViewById(R.id.editTextDurationMinutes);
        editTextDurationSeconds = rootView.findViewById(R.id.editTextDurationSeconds);
        editTextGoalHours = rootView.findViewById(R.id.editTextGoalHours);
        editTextGoalMinutes = rootView.findViewById(R.id.editTextGoalMinutes);
        editTextGoalSeconds = rootView.findViewById(R.id.editTextGoalSeconds);
        btnSelectDate = rootView.findViewById(R.id.btnSelectDate);
        totalDistanceTextView = rootView.findViewById(R.id.totalDistanceTextView);

        // Set click listener for invoking the saveWorkout method
        Button btnSaveWorkout = rootView.findViewById(R.id.btnSaveWorkout);
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        // Set click listener for invoking showDatePickerDialog method
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

    // Exercise list for the spinner
    private ArrayList<String> getExerciseList() {
        return new ArrayList<>(Arrays.asList("Walking", "Running", "Cycling", "Weight lifting", "Body exercises"));
    }

    // Method is called when the user wants to save a workout
    private void saveWorkout() {
        // Get selected workout details
        String selectedExercise = exerciseSpinner.getSelectedItem().toString();
        String durationHours = editTextDurationHours.getText().toString();
        String durationMinutes = editTextDurationMinutes.getText().toString();
        String durationSeconds = editTextDurationSeconds.getText().toString();
        String goalHours = editTextGoalHours.getText().toString();
        String goalMinutes = editTextGoalMinutes.getText().toString();
        String goalSeconds = editTextGoalSeconds.getText().toString();

        // Validate input for exercise
        if (TextUtils.isEmpty(selectedExercise) || TextUtils.isEmpty(durationHours) || TextUtils.isEmpty(durationMinutes) || TextUtils.isEmpty(durationSeconds)) {
            Toast.makeText(requireContext(), "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validate input for goal exercise
        if (TextUtils.isEmpty(goalHours) || TextUtils.isEmpty(goalMinutes) || TextUtils.isEmpty(goalSeconds)) {
            Toast.makeText(requireContext(), "Please enter a goal duration", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new instance of WorkoutEntity with the user-entered details
        WorkoutEntity workoutEntity = new WorkoutEntity(
                selectedExercise,
                Integer.parseInt(durationHours),
                Integer.parseInt(durationMinutes),
                Integer.parseInt(durationSeconds),
                Integer.parseInt(goalHours),
                Integer.parseInt(goalMinutes),
                Integer.parseInt(goalSeconds),
                selectedDate,
                0.0,
                0.0
        );

        // Save the workout to the database asynchronously using AsyncTask
        new SaveWorkoutAsyncTask().execute(workoutEntity);

        // Display a confirmation message
        String toastMessage = "Workout Saved:\n" + workoutEntity.toString();

        // Compare actual duration with goal duration
        if (compareDurationWithGoal(workoutEntity.getDurationInSeconds(), workoutEntity)) {
            toastMessage += "\nYou are doing a great job!";
        } else {
            toastMessage += "\nKeep on pushing!";
        }
        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();

        // Update the workout history UI
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).viewWorkouts(null);
        }
    }

    // Helper method to compare actual duration with goal duration
    private boolean compareDurationWithGoal(int actualDurationSeconds, WorkoutEntity workoutEntity) {
        int goalSeconds = workoutEntity.getGoalInSeconds();

        // Compare the two durations
        return actualDurationSeconds >= goalSeconds;
    }

    // Method to get current date in format yyyy-MM-dd
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
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

    // Method to update the totalDistance TextView with new total distance
    private void updateTotalDistance(double distance) {
        double totalDistance = Double.parseDouble(totalDistanceTextView.getText().toString()) + distance;
        totalDistanceTextView.setText(String.format(Locale.getDefault(), "%.2f km", totalDistance / 1000));
    }

    // Method for saving a WorkoutEntity to the database in the background
    private static class SaveWorkoutAsyncTask extends AsyncTask<WorkoutEntity, Void, Void> {
        @Override
        // Execute in the background in separate thread
        protected Void doInBackground(WorkoutEntity... workoutEntities) {
            // Access WorkoutDatabase through MyApplication class and insert the WorkoutEntity in the database
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().insert(workoutEntities[0]);
            return null;
        }
    }
}