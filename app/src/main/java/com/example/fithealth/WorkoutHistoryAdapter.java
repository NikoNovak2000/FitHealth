package com.example.fithealth;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;


import java.util.List;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder> {
    private List<WorkoutEntity> workoutList;

    public WorkoutHistoryAdapter(List<WorkoutEntity> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout_history, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutEntity workoutEntity = workoutList.get(position);
        holder.bind(workoutEntity);
    }

    @Override
    public int getItemCount() {
        return workoutList != null ? workoutList.size() : 0;
    }

    // Set the workout list
    public void setWorkoutList(List<WorkoutEntity> workoutList) {
        this.workoutList = workoutList;
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewExercise;
        private TextView textViewDuration;
        private TextView textViewGoal;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewExercise = itemView.findViewById(R.id.textViewExercise);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewGoal = itemView.findViewById(R.id.textViewGoal);
        }

        public void bind(WorkoutEntity workoutEntity) {
            textViewExercise.setText(workoutEntity.getExercise());
            textViewDuration.setText(workoutEntity.getDuration());
            textViewGoal.setText(workoutEntity.getGoal());
        }
    }
}