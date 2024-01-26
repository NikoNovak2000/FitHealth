package com.example.fithealth;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Defines a Room Database class called WorkoutDatabase for handling the persistence of workout-related data

@Database(entities = {WorkoutEntity.class}, version = 5, exportSchema = false)
public abstract class WorkoutDatabase extends RoomDatabase {
    public static final Migration MIGRATION_1_2 = new Migration_1_2();
    private static WorkoutDatabase instance;

    // Abstract method declares DAO interface for accessing the database operations related to WorkoutEntity
    public abstract WorkoutDataAccessObject workoutDataAccessObject();

    // Callback class provides methodes to be called during the creation and opening of the db
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

        }
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Check if database has been opened
            Log.d("WorkoutDatabase", "Database has been opened!");
        }
    };
    // Create or retrieve an instance of WorkoutDatabase, if instance null create instance, if not retrieve
    public static synchronized WorkoutDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            WorkoutDatabase.class, "workout_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }
}
