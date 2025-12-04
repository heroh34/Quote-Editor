package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.editor.app.R;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class GradientViewBinder {
    public interface OnGradientSelectedListener {
        void onGradientSelected(GradientItem gradient, int position);
    }

    private final List<GradientItem> gradients;
    private final OnGradientSelectedListener listener;
    private final LinearLayout container;
    private int selectedPosition = -1;
    private final List<ViewHolder> viewHolders = new ArrayList<>();

    public GradientViewBinder(LinearLayout container, List<GradientItem> gradients, OnGradientSelectedListener listener) {
        this.container = container;
        this.gradients = gradients;
        this.listener = listener;
        bindGradients();
    }

    private void bindGradients() {
        container.removeAllViews();
        viewHolders.clear();

        for (int i = 0; i < gradients.size(); i++) {
            GradientItem gradientItem = gradients.get(i);
            int position = i;

            // Inflate the gradient item view
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_gradient, container, false);

            ViewHolder holder = new ViewHolder(view, position);
            viewHolders.add(holder);
            holder.bind(gradientItem);

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
        View gradientView;
        GradientItem gradientItem;
        int position;

        ViewHolder(View itemView, int position) {
            this.position = position;
            cardView = itemView.findViewById(R.id.gradientCard);
            gradientView = itemView.findViewById(R.id.gradientView);
        }

        void bind(GradientItem item) {
            this.gradientItem = item;

            // Create gradient drawable
            GradientDrawable drawable = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    gradientItem.getColors()
            );
            drawable.setCornerRadius(12f);
            gradientView.setBackground(drawable);

            // Update selection state
            updateSelection(position == selectedPosition);

            // Click listener
            cardView.setOnClickListener(v -> {
                setSelectedPosition(position);
                if (listener != null) {
                    listener.onGradientSelected(gradientItem, position);
                }
            });
        }

        void updateSelection(boolean isSelected) {
            if (isSelected) {
                cardView.setStrokeWidth(6);
                cardView.setStrokeColor(Color.WHITE);
                cardView.setCardElevation(8f);
            } else {
                cardView.setStrokeWidth(0);
                cardView.setCardElevation(2f);
            }
        }
    }
}
