package com.example.fithealth;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewWorkouts(View view) {
        // Create a new instance of the fragment workoutHistory
        WorkoutHistoryFragment workoutHistoryFragment = new WorkoutHistoryFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, workoutHistoryFragment)
                .addToBackStack(null)
                .commit();
    }
    public void savePreviousWorkout(View view) {
        // Create a new instance of the fragment startWorkout
        SavePreviousWorkoutFragment savePreviousWorkoutFragment = new SavePreviousWorkoutFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, savePreviousWorkoutFragment)
                .addToBackStack(null)
                .commit();
    }
}