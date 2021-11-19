package com.example.hypercoachinterface.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.routine.RoutineDetailActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private static final String TAG = "ItemAdapter";
    protected final List<RoutineSummary> dataSet;

    public ItemAdapter(List<RoutineSummary> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + dataSet.get(position).getImgSrc());
        Log.d(TAG, "onBindViewHolder: " + dataSet.get(position).getName() + " " + dataSet.get(position).getImgSrc());
        holder.textView.setText(dataSet.get(position).getName());
        holder.favCount.setText(dataSet.get(position).getFavCount().toString() + " " + new String(Character.toChars(0x2605)));
        Utils.setImageFromBase64(holder.imageView, dataSet.get(position).getImgSrc());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView textView, favCount;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(view1 -> {
                Log.d(TAG, "ViewHolder: " + dataSet.get(getAdapterPosition()).getRoutineId());

                Integer routineId = dataSet.get(getAdapterPosition()).getRoutineId();
                Context context = getCardView().getContext();
                Intent intent = new Intent(context, RoutineDetailActivity.class);
                intent.setData(Uri.parse("http://hypercoachinterface.com/routine?id=" + routineId));
                context.startActivity(intent);
                // context.finish();
            });
            cardView = itemView.findViewById(R.id.cardview);
            textView = (TextView)itemView
                    .findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            favCount = itemView.findViewById(R.id.favCount);

        }

        public CardView getCardView() {
            return cardView;
        }
    }
}
