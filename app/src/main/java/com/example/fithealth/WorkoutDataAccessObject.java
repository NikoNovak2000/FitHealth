package com.example.fithealth;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

// Data Access Object (Dao) interface for interacting with the workout data in a Room database
@Dao
public interface WorkoutDataAccessObject {
    // Method used to insert a WorkoutEntity in the db
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WorkoutEntity workout);
    // Method used to retrieve all workouts from db
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    List<WorkoutEntity> getAllWorkouts();
    // Method used to delete all workouts from db
    @Query("DELETE FROM workouts")
    void clearAllWorkouts();
}
