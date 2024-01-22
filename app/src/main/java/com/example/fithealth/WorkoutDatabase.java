package com.example.fithealth;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {WorkoutEntity.class}, version = 1)
public abstract class WorkoutDatabase extends RoomDatabase {
    private static WorkoutDatabase instance;

    public abstract WorkoutDataAccessObject workoutDataAccessObject();

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
