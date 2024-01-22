package com.example.fithealth;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workouts")
public class WorkoutEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String exercise;
    private String duration;
    private String goal;
    private String date;

    public WorkoutEntity(String exercise, String duration, String goal, String date) {
        this.exercise = exercise;
        this.duration = duration;
        this.goal = goal;
        this.date = date;
    }

    // Constructors, getters, and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExercise() {
        return exercise;
    }

    public void setExercise(String exercise) {
        this.exercise = exercise;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Workout: " + exercise +
                "\nDuration: " + duration +
                "\nGoal: " + goal +
                "\nDate: " + date;
    }
}
