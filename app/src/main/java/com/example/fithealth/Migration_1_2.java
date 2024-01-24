package com.example.fithealth;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_1_2 extends Migration {
    public Migration_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // Perform necessary migration steps

        // Example: Add a new column to the existing table
        database.execSQL("ALTER TABLE workouts ADD COLUMN goal_hours INTEGER DEFAULT 0");
        database.execSQL("ALTER TABLE workouts ADD COLUMN goal_minutes INTEGER DEFAULT 0");
        database.execSQL("ALTER TABLE workouts ADD COLUMN goal_seconds INTEGER DEFAULT 0");
    }
}