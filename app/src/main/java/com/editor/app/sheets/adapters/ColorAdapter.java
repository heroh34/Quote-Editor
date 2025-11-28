package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.sheets.models.ColorItem;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    public interface OnColorSelectedListener {
        void onColorSelected(int color, int position);
    }

    private final List<ColorItem> colors;
    private final OnColorSelectedListener listener;
    private int selectedPosition = -1;

    public ColorAdapter(List<ColorItem> colors, OnColorSelectedListener listener) {
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
        ColorItem colorItem = colors.get(position);
        holder.bind(colorItem, position);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(selectedPosition);
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        View colorView;

        ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.colorCard);
            colorView = itemView.findViewById(R.id.colorView);
        }

        void bind(ColorItem colorItem, int position) {
            // Set the color
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(colorItem.getColor());
            drawable.setCornerRadius(12f);
            colorView.setBackground(drawable);

            // Show selection state
            if (position == selectedPosition) {
                cardView.setStrokeWidth(8);
                cardView.setStrokeColor(Color.WHITE);
                cardView.setCardElevation(8f);
            } else {
                cardView.setStrokeWidth(0);
                cardView.setCardElevation(2f);
            }

            // Click listener
            cardView.setOnClickListener(v -> {
                setSelectedPosition(position);
                if (listener != null) {
                    listener.onColorSelected(colorItem.getColor(), position);
                }
            });
        }
    }
}
