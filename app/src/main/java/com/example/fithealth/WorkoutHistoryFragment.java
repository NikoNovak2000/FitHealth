package com.example.fithealth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkoutHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private WorkoutHistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_history, container, false);

        // Set up RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerViewWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Create an empty adapter for now
        adapter = new WorkoutHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Fetch workout data from the database asynchronously
        new FetchWorkoutDataAsyncTask().execute();

        // Set click listener for invoking the showClearHistoryConfirmationDialog method
        Button btnClearHistory = rootView.findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryConfirmationDialog();
            }
        });

        return rootView;
    }

    // Fetch workout data from the db in background
    private class FetchWorkoutDataAsyncTask extends AsyncTask<Void, Void, List<WorkoutEntity>> {
        @Override
        protected List<WorkoutEntity> doInBackground(Void... voids) {
            // Fetch workout data from the database
            return MyApplication.getWorkoutDatabase().workoutDataAccessObject().getAllWorkouts();
        }

        // Update WorkoutHistoryAdapter with fetched workout data and notify the adapter about data being changed
        @Override
        protected void onPostExecute(List<WorkoutEntity> workoutData) {
            // Update the adapter with the fetched workout data
            adapter.setWorkoutList(workoutData);
            adapter.notifyDataSetChanged();
        }
    }

    // Method displays a confirmation dialog asking the user to confirm clearing the history
    private void showClearHistoryConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Clear History");
        builder.setMessage("Are you sure you want to clear the workout history?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call method to clear workout history
                clearWorkoutHistory();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, close dialog
            }
        });
        builder.show();
    }

    // Method initiates the process of clearing the workout history by showing the confirmation dialog
    private void clearWorkoutHistory() {
        // Clear workout history (delete all records from the database)
        new ClearWorkoutHistoryAsyncTask().execute();
    }

    // Clears the workout history from the database and fetches the updated workout data
    private class ClearWorkoutHistoryAsyncTask extends AsyncTask<Void, Void, List<WorkoutEntity>> {
        @Override
        protected List<WorkoutEntity> doInBackground(Void... voids) {
            // Clear workout history from the database
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().clearAllWorkouts();
            // Fetch the updated workout data after clearing
            return MyApplication.getWorkoutDatabase().workoutDataAccessObject().getAllWorkouts();
        }

        // Log msg, updates the WorkoutHistoryAdapter with the updated workout data, notify the adapter
        @Override
        protected void onPostExecute(List<WorkoutEntity> workoutData) {
            Log.d("ClearWorkoutHistory", "Workout history cleared");
            // Update the adapter with the fetched workout data
            adapter.setWorkoutList(workoutData);
            adapter.notifyDataSetChanged();
        }
    }
}