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

        // Find Clear Button
        Button btnClearHistory = rootView.findViewById(R.id.btnClearHistory);
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to show confirmation dialog
                showClearHistoryConfirmationDialog();
            }
        });

        return rootView;
    }

    private class FetchWorkoutDataAsyncTask extends AsyncTask<Void, Void, List<WorkoutEntity>> {
        @Override
        protected List<WorkoutEntity> doInBackground(Void... voids) {
            // Fetch workout data from the database
            return MyApplication.getWorkoutDatabase().workoutDataAccessObject().getAllWorkouts();
        }

        @Override
        protected void onPostExecute(List<WorkoutEntity> workoutData) {
            Log.d("FetchWorkoutData", "Updating adapter with fetched data");
            // Update the adapter with the fetched workout data
            adapter.setWorkoutList(workoutData);
            adapter.notifyDataSetChanged();
        }
    }

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

    private void clearWorkoutHistory() {
        // Clear workout history (delete all records from the database)
        new ClearWorkoutHistoryAsyncTask().execute();
    }

    private class ClearWorkoutHistoryAsyncTask extends AsyncTask<Void, Void, List<WorkoutEntity>> {
        @Override
        protected List<WorkoutEntity> doInBackground(Void... voids) {
            // Clear workout history from the database
            MyApplication.getWorkoutDatabase().workoutDataAccessObject().clearAllWorkouts();
            // Fetch the updated workout data after clearing
            return MyApplication.getWorkoutDatabase().workoutDataAccessObject().getAllWorkouts();
        }

        @Override
        protected void onPostExecute(List<WorkoutEntity> workoutData) {
            Log.d("ClearWorkoutHistory", "Workout history cleared");
            // Update the adapter with the fetched workout data
            adapter.setWorkoutList(workoutData);
            adapter.notifyDataSetChanged();
        }
    }
}