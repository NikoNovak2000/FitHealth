package com.example.fithealth;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workouts")
public class WorkoutEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String exercise;
    private int durationHours;
    private int durationMinutes;
    private int durationSeconds;
    private int goalHours;
    private int goalMinutes;
    private int goalSeconds;
    private String date;
    private double totalDistance; // Change the type to double
    private double averageSpeed;

    public WorkoutEntity(String exercise, int durationHours, int durationMinutes, int durationSeconds,
                         int goalHours, int goalMinutes, int goalSeconds, String date, double totalDistance, double averageSpeed) {
        this.exercise = exercise;
        this.durationHours = durationHours;
        this.durationMinutes = durationMinutes;
        this.durationSeconds = durationSeconds;
        this.goalHours = goalHours;
        this.goalMinutes = goalMinutes;
        this.goalSeconds = goalSeconds;
        this.date = date;
        this.totalDistance = totalDistance;
        this.averageSpeed = averageSpeed;
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

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(int durationHours) {
        this.durationHours = durationHours;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getGoalHours() {
        return goalHours;
    }

    public void setGoalHours(int goalHours) {
        this.goalHours = goalHours;
    }

    public int getGoalMinutes() {
        return goalMinutes;
    }

    public void setGoalMinutes(int goalMinutes) {
        this.goalMinutes = goalMinutes;
    }

    public int getGoalSeconds() {
        return goalSeconds;
    }

    public void setGoalSeconds(int goalSeconds) {
        this.goalSeconds = goalSeconds;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    // Helper method to get goal duration in seconds
    public int getGoalInSeconds() {
        return goalHours * 3600 + goalMinutes * 60 + goalSeconds;
    }

    // Helper method to get workout duration in seconds
    public int getDurationInSeconds() {
        return durationHours * 3600 + durationMinutes * 60 + durationSeconds;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getFormattedAverageSpeed() {
        // Format average speed to show one decimal place
        return String.format("%.2f km/h", averageSpeed);
    }

    @Override
    public String toString() {
        return "Workout: " + exercise +
                "\nDuration: " + durationHours + "h " + durationMinutes + "m " + durationSeconds + "s" +
                "\nGoal: " + goalHours + "h " + goalMinutes + "m " + goalSeconds + "s" +
                "\nDate: " + date +
                "\nTotal Distance: " + totalDistance + " m" +
                "\n Average Speed: " + getFormattedAverageSpeed();
    }
}