package com.example.fithealth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class WorkoutHistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_history, container, false);

        // Dummy workout data (replace this with actual data retrieval)
        ArrayList<String> workoutData = new ArrayList<>(Arrays.asList("Workout 1", "Workout 2", "Workout 3"));

        // Populate the ListView with workout data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, workoutData);
        ListView listView = rootView.findViewById(R.id.listViewWorkouts);
        listView.setAdapter(adapter);

        return rootView;
    }
}
