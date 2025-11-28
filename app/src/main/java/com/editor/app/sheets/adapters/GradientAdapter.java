package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class GradientAdapter extends RecyclerView.Adapter<GradientAdapter.GradientViewHolder> {
    public interface OnGradientSelectedListener {
        void onGradientSelected(GradientItem gradient, int position);
    }

    private final List<GradientItem> gradients;
    private final OnGradientSelectedListener listener;
    private int selectedPosition = -1;

    public GradientAdapter(List<GradientItem> gradients, OnGradientSelectedListener listener) {
        this.gradients = gradients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GradientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gradient, parent, false);
        return new GradientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GradientViewHolder holder, int position) {
        GradientItem gradientItem = gradients.get(position);
        holder.bind(gradientItem, position);
    }

    @Override
    public int getItemCount() {
        return gradients.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(selectedPosition);
    }

    public class GradientViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        View gradientView;

        GradientViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.gradientCard);
            gradientView = itemView.findViewById(R.id.gradientView);
        }

        void bind(GradientItem gradientItem, int position) {
            // Create gradient drawable
            GradientDrawable drawable = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    gradientItem.getColors()
            );
            drawable.setCornerRadius(12f);
            gradientView.setBackground(drawable);

            // Show selection state
            if (position == selectedPosition) {
                cardView.setStrokeWidth(6);
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
                    listener.onGradientSelected(gradientItem, position);
                }
            });
        }
    }
}
