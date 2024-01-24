package com.example.fithealth;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        holder.bind(workoutEntity, this);
    }

    @Override
    public int getItemCount() {
        return workoutList != null ? workoutList.size() : 0;
    }

    // Set the workout list
    public void setWorkoutList(List<WorkoutEntity> workoutList) {
        this.workoutList = workoutList;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewWorkout;
        private TextView textViewDuration;
        private TextView textViewGoal;
        private TextView textViewDate;
        private TextView textViewMessage;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWorkout = itemView.findViewById(R.id.textViewWorkout);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            textViewGoal = itemView.findViewById(R.id.textViewGoal);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }

        public void bind(WorkoutEntity workoutEntity, WorkoutHistoryAdapter adapter) {
            textViewWorkout.setText("Workout: " + workoutEntity.getExercise());

            // Pass duration as a long
            long actualDuration = workoutEntity.getDurationInSeconds();
            String formattedDuration = formatDuration(actualDuration);

            textViewDuration.setText("Duration: " + formattedDuration);

            textViewGoal.setText("Goal duration: " + formatGoalDuration(workoutEntity));
            textViewDate.setText("Date: " + formatDate(workoutEntity.getDate()));

            // Add appropriate message based on duration and goal
            if (compareDurationWithGoal(actualDuration, workoutEntity)) {
                textViewMessage.setText("You are doing a great job!");
            } else {
                textViewMessage.setText("Keep on pushing!");
            }
        }

        // Helper method compares actual duration with goal duration
        private boolean compareDurationWithGoal(long actualDuration, WorkoutEntity workoutEntity) {
            int goalInSeconds = workoutEntity.getGoalHours() * 3600 + workoutEntity.getGoalMinutes() * 60 + workoutEntity.getGoalSeconds();
            return actualDuration >= goalInSeconds;
        }

        // Helper method to format duration in the desired format
        private String formatDuration(long duration) {
            int hours = (int) (duration / 3600);
            int minutes = (int) ((duration % 3600) / 60);
            int seconds = (int) (duration % 60);

            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }

        // Helper method to format goal duration
        private String formatGoalDuration(WorkoutEntity workoutEntity) {
            return String.format(Locale.getDefault(), "%02dh %02dm %02ds",
                    workoutEntity.getGoalHours(), workoutEntity.getGoalMinutes(), workoutEntity.getGoalSeconds());
        }

        private String formatDate(String inputDate) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = inputFormat.parse(inputDate);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return inputDate; // Return the original date if parsing fails
            }
        }
    }
}