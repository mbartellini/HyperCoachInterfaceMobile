package com.example.hypercoachinterface.ui.routine;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private static final String TAG = "ExerciseAdapter";
    protected final List<RoutineExercise> dataSet;

    public ExerciseAdapter(List<RoutineExercise> dataSet) {
        this.dataSet = dataSet;
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

        // holder.title.setText(dataSet.get(position).);
        holder.reps.setText(String.format("%d %s", dataSet.get(position).getLimit(), dataSet.get(position).getLimitType()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView reps;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = (TextView)itemView
                    .findViewById(R.id.exercise_title);
            reps = itemView.findViewById(R.id.exercise_reps);
        }

    }
}
