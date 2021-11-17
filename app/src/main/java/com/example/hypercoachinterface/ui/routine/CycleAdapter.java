package com.example.hypercoachinterface.ui.routine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hypercoachinterface.R;

import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.ui.routine.RoutineDetailActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.ViewHolder> {

    private static final String TAG = "CycleAdapter";
    protected final List<RoutineCycle> dataSet;

    public CycleAdapter(List<RoutineCycle> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cycle_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CycleAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");
        // holder.getCardView().textholder.textView.setText(dataSet.get(position).getName());
        holder.title.setText(dataSet.get(position).getName());
        holder.reps.setText(String.format("x%d rep%s.", dataSet.get(position).getRepetitions(), dataSet.get(position).getRepetitions() > 1 ? "s" : ""));
        List<RoutineExercise> exerciseList = new ArrayList<>(dataSet.get(position).getExercises());
        ExerciseAdapter adapter = new ExerciseAdapter(exerciseList);
        holder.exercises.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView title;
        private final TextView reps;
        private final RecyclerView exercises;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview);
            title = (TextView)itemView
                    .findViewById(R.id.cycle_title);
            reps = itemView.findViewById(R.id.cycle_reps);
            exercises = itemView.findViewById(R.id.cycle_exercises);
        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
