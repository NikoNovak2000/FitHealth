package com.example.fithealth;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDataAccessObject {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WorkoutEntity workout);

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    List<WorkoutEntity> getAllWorkouts();

    @Query("DELETE FROM workouts")
    void clearAllWorkouts();
}
