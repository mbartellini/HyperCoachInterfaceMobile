package com.example.hypercoachinterface.ui.routine;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Exercise;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;

import java.util.List;
import java.util.Map;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private static final String TAG = "ExerciseAdapter";
    protected final List<RoutineExercise> dataSet;
    protected final Map<Integer, Exercise> exerciseMap;

    public ExerciseAdapter(List<RoutineExercise> dataSet, Map<Integer, Exercise> exerciseMap) {
        this.dataSet = dataSet;
        this.exerciseMap = exerciseMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ViewHolder holder, int position) {
        if (exerciseMap.getOrDefault(dataSet.get(position).getId(), null) != null)
            holder.title.setText( exerciseMap.get( dataSet.get(position).getId() ).getName());

        holder.reps.setText(String.format("%d %s", dataSet.get(position).getLimit(), dataSet.get(position).getLimitType()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView title;
        private final TextView reps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.itemView = itemView;
            title = (TextView)itemView
                    .findViewById(R.id.exercise_title);
            reps = itemView.findViewById(R.id.exercise_reps);
        }

    }
}
