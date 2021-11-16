package com.example.hypercoachinterface.ui.favorites.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FavItemAdapter extends RecyclerView.Adapter<FavItemAdapter.ViewHolder> {

    private static final String TAG = "ItemAdapter";
    private List<Routine> dataSet;

    public FavItemAdapter(List<Routine> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavItemAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.textView.setText(dataSet.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(view1 -> {
                getCardView().setCardBackgroundColor(Color.BLACK);
                Snackbar.make(itemView, getItemCount() + " elements", BaseTransientBottomBar.LENGTH_LONG).show();
            });
            cardView = itemView.findViewById(R.id.cardview);
            textView = (TextView)itemView
                    .findViewById(R.id.textView);
        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
