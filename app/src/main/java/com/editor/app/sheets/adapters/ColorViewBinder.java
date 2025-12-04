package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.editor.app.R;
import com.editor.app.sheets.models.ColorItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ColorViewBinder {
    public interface OnColorSelectedListener {
        void onColorSelected(int color, int position);
    }

    private final List<ColorItem> colors;
    private final OnColorSelectedListener listener;
    private final LinearLayout container;
    private int selectedPosition = -1;
    private final List<ViewHolder> viewHolders = new ArrayList<>();

    public ColorViewBinder(LinearLayout container, List<ColorItem> colors, OnColorSelectedListener listener) {
        this.container = container;
        this.colors = colors;
        this.listener = listener;
        bindColors();
    }

    private void bindColors() {
        container.removeAllViews();
        viewHolders.clear();

        for (int i = 0; i < colors.size(); i++) {
            ColorItem colorItem = colors.get(i);
            int position = i;

            // Inflate the color item view
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_color, container, false);

            ViewHolder holder = new ViewHolder(view, position);
            viewHolders.add(holder);
            holder.bind(colorItem);

            container.addView(view);
        }
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        // Update previous selection
        if (previousPosition != -1 && previousPosition < viewHolders.size()) {
            viewHolders.get(previousPosition).updateSelection(false);
        }

        // Update new selection
        if (selectedPosition != -1 && selectedPosition < viewHolders.size()) {
            viewHolders.get(selectedPosition).updateSelection(true);
        }
    }

    private class ViewHolder {
        MaterialCardView cardView;
        View colorView;
        ColorItem colorItem;
        int position;

        ViewHolder(View itemView, int position) {
            this.position = position;
            cardView = itemView.findViewById(R.id.colorCard);
            colorView = itemView.findViewById(R.id.colorView);
        }

        void bind(ColorItem item) {
            this.colorItem = item;

            // Set the color
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setColor(colorItem.getColor());
            drawable.setCornerRadius(12f);
            colorView.setBackground(drawable);

            // Update selection state
            updateSelection(position == selectedPosition);

            // Click listener
            cardView.setOnClickListener(v -> {
                setSelectedPosition(position);
                if (listener != null) {
                    listener.onColorSelected(colorItem.getColor(), position);
                }
            });
        }

        void updateSelection(boolean isSelected) {
            if (isSelected) {
                cardView.setStrokeWidth(8);
                cardView.setStrokeColor(Color.WHITE);
                cardView.setCardElevation(8f);
            } else {
                cardView.setStrokeWidth(0);
                cardView.setCardElevation(2f);
            }
        }
    }
}
