package com.editor.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.models.CanvasSize;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class ResizeAdapter extends RecyclerView.Adapter<ResizeAdapter.ResizeViewHolder> {
    private final List<CanvasSize> sizes;
    private final OnSizeClickListener listener;
    private int selectedPosition = 0;

    public interface OnSizeClickListener {
        void onSizeClick(CanvasSize size);
    }

    public ResizeAdapter(List<CanvasSize> sizes, OnSizeClickListener listener) {
        this.sizes = sizes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resize, parent, false);
        return new ResizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResizeViewHolder holder, int position) {
        CanvasSize size = sizes.get(position);

        holder.sizeName.setText(size.getName());
        holder.sizeDimensions.setText(size.getDimensions());

        if (size.getIconResId() != 0) {
            holder.sizeIcon.setImageResource(size.getIconResId());
        }

        holder.sizeCheckbox.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onSizeClick(size);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    public static class ResizeViewHolder extends RecyclerView.ViewHolder {
        ImageView sizeIcon;
        TextView sizeName;
        TextView sizeDimensions;
        MaterialCheckBox sizeCheckbox;

        ResizeViewHolder(@NonNull View itemView) {
            super(itemView);
            sizeIcon = itemView.findViewById(R.id.sizeIcon);
            sizeName = itemView.findViewById(R.id.sizeName);
            sizeDimensions = itemView.findViewById(R.id.sizeDimensions);
            sizeCheckbox = itemView.findViewById(R.id.sizeCheckbox);
        }
    }
}
