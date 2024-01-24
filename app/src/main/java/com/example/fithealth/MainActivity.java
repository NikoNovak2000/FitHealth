package com.example.fithealth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set click listener for the Track Workout button
        Button btnTrackWorkout = findViewById(R.id.btnTrackWorkout);
        btnTrackWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTrackWorkoutFragment();
            }
        });
    }
    private void loadTrackWorkoutFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TrackWorkoutFragment())
                .addToBackStack(null)
                .commit();
    }

    public void savePreviousWorkout(View view) {
        // Create a new instance of the fragment startPreviousWorkout
        SavePreviousWorkoutFragment savePreviousWorkoutFragment = new SavePreviousWorkoutFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, savePreviousWorkoutFragment)
                .addToBackStack(null)
                .commit();
    }

    public void viewWorkouts(View view) {
        // Create a new instance of the fragment workoutHistory
        WorkoutHistoryFragment workoutHistoryFragment = new WorkoutHistoryFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, workoutHistoryFragment)
                .addToBackStack(null)
                .commit();
    }

}