package com.example.fithealth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class StartWorkoutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_workout, container, false);

        // Initialize exercise spinner
        Spinner exerciseSpinner = rootView.findViewById(R.id.spinnerExercise);
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

        return rootView;
    }

    // Dummy data for exercise spinner (replace with actual data)
    private ArrayList<String> getExerciseList() {
        return new ArrayList<>(Arrays.asList("Exercise 1", "Exercise 2", "Exercise 3"));
    }

    // Method to handle saving the workout
    public void saveWorkout() {
        // logic to save workout details
        // For now, display a toast message
        Toast.makeText(requireContext(), "Workout Saved!", Toast.LENGTH_SHORT).show();
    }
}
