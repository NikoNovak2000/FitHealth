package com.example.fithealth;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
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
    private Spinner exerciseSpinner;
    private EditText editTextGoalHours;
    private EditText editTextGoalMinutes;
    private EditText editTextGoalSeconds;
    private Chronometer chronometer;
    private long startTime = 0;
    private long pausedTime = 0;
    private int durationHours;
    private int durationMinutes;
    private int durationSeconds;
    private String selectedDate;
    private boolean isTimerRunning = false;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView totalDistanceTextView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private double totalDistance = 0;
    private TextView averageSpeedTextView;
    private double averageSpeed = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_workout, container, false);

        exerciseSpinner = rootView.findViewById(R.id.spinnerExercise);
        chronometer = rootView.findViewById(R.id.chronometer);
        editTextGoalHours = rootView.findViewById(R.id.editTextGoalHours);
        editTextGoalMinutes = rootView.findViewById(R.id.editTextGoalMinutes);
        editTextGoalSeconds = rootView.findViewById(R.id.editTextGoalSeconds);
        totalDistanceTextView = rootView.findViewById(R.id.totalDistanceTextView);
        averageSpeedTextView = rootView.findViewById(R.id.averageSpeedTextView);

        ArrayAdapter<String> exerciseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, getExerciseList());
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(exerciseAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        selectedDate = getCurrentDate();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    onLocationChanged(locationResult.getLastLocation());
                }
            }
        };

        Button btnStartStopTimer = rootView.findViewById(R.id.btnStartStopTimer);
        btnStartStopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopTimer(v);
            }
        });

        Button btnSaveWorkout = rootView.findViewById(R.id.btnSaveWorkout);
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
                        selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                onLocationChanged(location);
            }
        }
    };

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

    private void startTimer() {
        if (!isTimerRunning) {
            startTime = SystemClock.elapsedRealtime() - pausedTime;
            chronometer.setBase(startTime);
            chronometer.start();
            isTimerRunning = true;
        }
    }

    private void stopTimer() {
        if (isTimerRunning) {
            chronometer.stop();
            pausedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isTimerRunning = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation != null) {
            float distance = lastLocation.distanceTo(location);
            updateTotalDistance(distance);
        }
        lastLocation = location;
        updateAverageSpeed();
    }

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

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void updateTotalDistance(float distance) {
        totalDistance += distance;
        updateTotalDistanceTextView();
    }

    private void updateTotalDistanceTextView() {
        if (totalDistanceTextView != null) {
            double totalDistanceInKm = totalDistance / 1000.0;
            totalDistanceTextView.setText(String.format(Locale.getDefault(), "%.2f km", totalDistanceInKm));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLocationUpdates();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }

    private void saveWorkout() {
        String selectedExercise = exerciseSpinner.getSelectedItem().toString();
        String goalHours = editTextGoalHours.getText().toString();
        String goalMinutes = editTextGoalMinutes.getText().toString();
        String goalSeconds = editTextGoalSeconds.getText().toString();

        if (TextUtils.isEmpty(selectedExercise)) {
            Toast.makeText(requireContext(), "Please select an exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(goalHours) || TextUtils.isEmpty(goalMinutes) || TextUtils.isEmpty(goalSeconds)) {
            Toast.makeText(requireContext(), "Please enter a goal duration", Toast.LENGTH_SHORT).show();
            return;
        }

        int goalHoursInt = Integer.parseInt(goalHours);
        int goalMinutesInt = Integer.parseInt(goalMinutes);
        int goalSecondsInt = Integer.parseInt(goalSeconds);

        String duration = getStoppedTimerDuration();

        String[] durationParts = duration.split(":");
        if (durationParts.length == 3) {
            durationHours = Integer.parseInt(durationParts[0]);
            durationMinutes = Integer.parseInt(durationParts[1]);
            durationSeconds = Integer.parseInt(durationParts[2]);
        } else {
            Toast.makeText(requireContext(), "Invalid duration format", Toast.LENGTH_SHORT).show();
            return;
        }

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

        navigateToWorkoutHistoryFragment();
    }

    private boolean compareDurationWithGoal(int actualDurationSeconds, WorkoutEntity workoutEntity) {
        return actualDurationSeconds >= workoutEntity.getGoalInSeconds();
    }

    private String getStoppedTimerDuration() {
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void navigateToWorkoutHistoryFragment() {
        if (getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new WorkoutHistoryFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

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

    private static class SaveWorkoutAsyncTask extends AsyncTask<WorkoutEntity, Void, Void> {
        @Override
        protected Void doInBackground(WorkoutEntity... workoutEntities) {
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().insert(workoutEntities[0]);
            return null;
        }
    }
}