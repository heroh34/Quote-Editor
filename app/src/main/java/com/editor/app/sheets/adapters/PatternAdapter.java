package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.sheets.models.PatternItem;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class PatternAdapter extends RecyclerView.Adapter<PatternAdapter.PatternViewHolder> {
    public interface OnPatternSelectedListener {
        void onPatternSelected(PatternItem pattern, int position);
    }

    private final List<PatternItem> patterns;
    private final OnPatternSelectedListener listener;
    private int selectedPosition = -1;

    public PatternAdapter(List<PatternItem> patterns, OnPatternSelectedListener listener) {
        this.patterns = patterns;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatternViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pattern, parent, false);
        return new PatternViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatternViewHolder holder, int position) {
        PatternItem patternItem = patterns.get(position);
        holder.bind(patternItem, position);
    }

    @Override
    public int getItemCount() {
        return patterns.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(selectedPosition);
    }

    public class PatternViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView patternImage;

        PatternViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.patternCard);
            patternImage = itemView.findViewById(R.id.patternImage);
        }

        void bind(PatternItem patternItem, int position) {
            // Set the pattern image
            patternImage.setImageResource(patternItem.getDrawableRes());

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
                    listener.onPatternSelected(patternItem, position);
                }
            });
        }
    }
}
