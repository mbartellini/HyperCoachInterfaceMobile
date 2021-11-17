package com.example.hypercoachinterface.ui.adapter;

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

import com.example.hypercoachinterface.ui.routine.RoutineDetailActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

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

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.textView.setText(dataSet.get(position).getName());
        if (dataSet.get(position).getImgSrc() != null) {
            String imageDataBytes = dataSet.get(position).getImgSrc().substring(dataSet.get(position).getImgSrc().indexOf(",")+1);
            byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageView.setImageBitmap(decodedByte);
        }
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

            itemView.setOnClickListener(view1 -> {
                Snackbar.make(itemView, "Routine " +  dataSet.get(getAdapterPosition()).getRoutineId() + " clicked", BaseTransientBottomBar.LENGTH_LONG).show();

                Context context = getCardView().getContext();
                Intent intent = new Intent(context, RoutineDetailActivity.class);
                Bundle b = new Bundle();
                b.putInt("routineId", dataSet.get(getAdapterPosition()).getRoutineId());
                intent.putExtras(b);
                context.startActivity(intent);
                // context.finish();
            });
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
