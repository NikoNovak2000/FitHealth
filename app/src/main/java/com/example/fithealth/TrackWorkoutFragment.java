package com.example.fithealth;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.location.LocationListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TrackWorkoutFragment extends Fragment implements LocationListener {
    private Chronometer chronometer;
    private int durationHours;
    private int durationMinutes;
    private int durationSeconds;
    private long startTime = 0;
    private long pausedTime = 0;
    private boolean isTimerRunning = false;
    private Location lastLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView totalDistanceTextView;
    private double totalDistance = 0;
    private TextView averageSpeedTextView;
    private double averageSpeed = 0;
    private Spinner exerciseSpinner;
    private EditText editTextGoalHours;
    private EditText editTextGoalMinutes;
    private EditText editTextGoalSeconds;
    private String selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_workout, container, false);

        // Find references to widget in the layout by its unique identifier
        chronometer = rootView.findViewById(R.id.chronometer);
        totalDistanceTextView = rootView.findViewById(R.id.totalDistanceTextView);
        averageSpeedTextView = rootView.findViewById(R.id.averageSpeedTextView);
        exerciseSpinner = rootView.findViewById(R.id.spinnerExercise);
        editTextGoalHours = rootView.findViewById(R.id.editTextGoalHours);
        editTextGoalMinutes = rootView.findViewById(R.id.editTextGoalMinutes);
        editTextGoalSeconds = rootView.findViewById(R.id.editTextGoalSeconds);
        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getExerciseList());
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        selectedDate = getCurrentDate();
        Button btnStartStopTimer = rootView.findViewById(R.id.btnStartStopTimer);
        Button btnSaveWorkout = rootView.findViewById(R.id.btnSaveWorkout);

        // LocationCallback object used to receive location updates form the fused location provider
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    onLocationChanged(locationResult.getLastLocation());
                }
            }
        };
        // Set click listener for invoking the startStopTimer method
        btnStartStopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopTimer(v);
            }
        });

        // Set click listener for invoking the saveWorkout method
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWorkout();
            }
        });

        return rootView;
    }

    // Method called when a btnStartStopTimer is clicked, if its already started then it stops
    public void startStopTimer(View view) {
        if (isTimerRunning) {
            // Stop the timer and calculate the elapsed time
            stopTimer();
            stopLocationUpdates();
        } else {
            // Start the timer and location updates
            startTimer();
            if (checkLocationPermission()) {
                startLocationUpdates();
            } else {
                Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Method starts and updates the timer, sets start time as the current elapsed real time minus the paused time
    private void startTimer() {
        if (!isTimerRunning) {
            startTime = SystemClock.elapsedRealtime() - pausedTime;
            chronometer.setBase(startTime);
            chronometer.start();
            isTimerRunning = true;
        }
    }
    // Method stops the timer and calculates the paused time
    private void stopTimer() {
        if (isTimerRunning) {
            chronometer.stop();
            pausedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isTimerRunning = false;
        }
    }
    // Method calculates the duration of the timer when it is stopped
    private String getStoppedTimerDuration() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        // Calculates hours, minutes and seconds based on the elapsed time
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Method returns a list of exercise names
    private ArrayList<String> getExerciseList() {
        return new ArrayList<>(Arrays.asList("Walking", "Running", "Cycling", "Weight lifting", "Body exercises"));
    }

    // Method for receiving location updates
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                onLocationChanged(location);
            }
        }
    };
    // Method called when the device's location is changed
    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation != null) {
            // Calculates distance between current location and last location, then updates total distance
            float distance = lastLocation.distanceTo(location);
            updateTotalDistance(distance);
        }
        // Calls updateAverageSpeed
        lastLocation = location;
        updateAverageSpeed();
    }
    // Method starts location updates using in the fused location provider, checks if location permission is granted before requesting updates
    private void startLocationUpdates() {
        if (checkLocationPermission()) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            // Handle the case where location permission is not granted
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
    // Method stops location updates
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    // Method checks if the location permission is granted,if not it requests the permission
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            return true;
        } else {
            // Permission is not granted, check if we should show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user asynchronously, and then check the response in onRequestPermissionsResult
                Toast.makeText(requireContext(), "Location permission required for this feature", Toast.LENGTH_SHORT).show();
            } else {
                // No explanation needed; request the permission
                try {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } catch (SecurityException e) {
                    e.printStackTrace();
                    // Handle the SecurityException here, if needed
                }
            }
            return false;
        }
    }
    // Method is part of the Android lifecycle and is called when the view is destroyed, stops the location updates
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
    }

    // Method calculates and updates the average speed based on the total distance and elapsed time
    private void updateAverageSpeed() {
        if (totalDistance > 0 && isTimerRunning) {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            double elapsedSeconds = elapsedMillis / 1000.0;

            if (elapsedSeconds > 0) {
                double elapsedHours = elapsedSeconds / 3600.0;
                elapsedHours = elapsedHours * 1000.0;
                double averageSpeed = totalDistance / elapsedHours;

                // Set the class variable averageSpeed
                this.averageSpeed = averageSpeed;

                // Display average speed in km/h
                averageSpeedTextView.setText(String.format(Locale.getDefault(), "Avg Speed: %.2f km/h", averageSpeed));

            } else {
                // Avoid division by zero and display an appropriate message
                averageSpeedTextView.setText("Avg Speed: N/A");
            }
        }
    }

    // Method updates the total distance based on the provided distance
    private void updateTotalDistance(float distance) {
        totalDistance += distance;
        updateTotalDistanceTextView();
    }

    // Method updates the total distance TextView
    private void updateTotalDistanceTextView() {
        if (totalDistanceTextView != null) {
            double totalDistanceInKm = totalDistance / 1000.0;
            totalDistanceTextView.setText(String.format(Locale.getDefault(), "%.2f km", totalDistanceInKm));
        }
    }

    // Method returns the current date in dd/MM/yyyy format
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    // Method called when the user wants to save a workout
    private void saveWorkout() {
        String selectedExercise = exerciseSpinner.getSelectedItem().toString();
        String goalHours = editTextGoalHours.getText().toString();
        String goalMinutes = editTextGoalMinutes.getText().toString();
        String goalSeconds = editTextGoalSeconds.getText().toString();
        int goalHoursInt = Integer.parseInt(goalHours);
        int goalMinutesInt = Integer.parseInt(goalMinutes);
        int goalSecondsInt = Integer.parseInt(goalSeconds);
        String duration = getStoppedTimerDuration();

        // Validate user inputs
        if (TextUtils.isEmpty(selectedExercise)) {
            Toast.makeText(requireContext(), "Please select an exercise", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(goalHours) || TextUtils.isEmpty(goalMinutes) || TextUtils.isEmpty(goalSeconds)) {
            Toast.makeText(requireContext(), "Please enter a goal duration", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] durationParts = duration.split(":");
        if (durationParts.length == 3) {
            durationHours = Integer.parseInt(durationParts[0]);
            durationMinutes = Integer.parseInt(durationParts[1]);
            durationSeconds = Integer.parseInt(durationParts[2]);
        } else {
            Toast.makeText(requireContext(), "Invalid duration format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create WorkoutEntity object
        WorkoutEntity workoutEntity = new WorkoutEntity(
                selectedExercise,
                durationHours,
                durationMinutes,
                durationSeconds,
                goalHoursInt,
                goalMinutesInt,
                goalSecondsInt,
                selectedDate,
                totalDistance,
                averageSpeed
        );

        // Set the average speed in the workoutEntity
        workoutEntity.setAverageSpeed(averageSpeed);

        // Save it to database
        new SaveWorkoutAsyncTask().execute(workoutEntity);

        String toastMessage = "Workout Saved:\n" +
                workoutEntity.toString() +
                "\nDistance: " + String.format(Locale.getDefault(), "%.3f km", totalDistance / 1000.0);

        if (compareDurationWithGoal(workoutEntity.getDurationInSeconds(), workoutEntity)) {
            toastMessage += "\nYou achieved your goal!";
        } else {
            toastMessage += "\nKeep pushing towards your goal!";
        }

        Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show();

        // Sets fragment to WorkoutHistory
        navigateToWorkoutHistoryFragment();
    }

    // Method compares duration and the goal duration
    private boolean compareDurationWithGoal(int actualDurationSeconds, WorkoutEntity workoutEntity) {
        return actualDurationSeconds >= workoutEntity.getGoalInSeconds();
    }

    // Method navigates to WorkoutHistoryFragment
    private void navigateToWorkoutHistoryFragment() {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new WorkoutHistoryFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    // Handles the result of the permission request for location updates
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Inner class extends AsyncTask, saves WorkoutEntity to the database in the background
    private static class SaveWorkoutAsyncTask extends AsyncTask<WorkoutEntity, Void, Void> {
        @Override
        protected Void doInBackground(WorkoutEntity... workoutEntities) {
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().insert(workoutEntities[0]);
            return null;
        }
    }
}