package com.editor.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    private final List<Integer> colors;
    private final OnColorClickListener listener;
    private int selectedPosition = -1;

    public interface OnColorClickListener {
        void onColorClick(int color);
    }

    public ColorAdapter(List<Integer> colors, OnColorClickListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        int color = colors.get(position);
        holder.colorView.setBackgroundColor(color);

        // Show selection indicator
        MaterialCardView cardView = (MaterialCardView) holder.itemView;
        if (position == selectedPosition) {
            cardView.setStrokeWidth(6);
            cardView.setStrokeColor(Color.parseColor("#6750A4"));
        } else {
            cardView.setStrokeWidth(0);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onColorClick(color);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorView);
        }
    }
}