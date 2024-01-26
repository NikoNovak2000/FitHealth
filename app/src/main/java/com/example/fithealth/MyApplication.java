package com.example.fithealth;
import android.app.Application;

public class MyApplication extends Application {
    // Static to ensure only one instance for entire app
    private static WorkoutDatabase workoutDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        // Retrieve or call database
        workoutDatabase = WorkoutDatabase.getInstance(this);
    }
    // Method providing access to the WorkoutDatabase
    public static WorkoutDatabase getWorkoutDatabase() {
        return workoutDatabase;
    }
}