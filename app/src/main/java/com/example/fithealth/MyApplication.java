package com.example.fithealth;
import android.app.Application;

public class MyApplication extends Application {
    private static WorkoutDatabase workoutDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        workoutDatabase = WorkoutDatabase.getInstance(this);
    }

    public static WorkoutDatabase getWorkoutDatabase() {
        return workoutDatabase;
    }
}