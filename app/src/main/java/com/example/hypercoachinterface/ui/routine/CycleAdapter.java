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
import com.example.hypercoachinterface.ui.routine.RoutineDetailActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.ViewHolder> {

    private static final String TAG = "ItemAdapter";
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
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardview);
            textView = (TextView)itemView
                    .findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
