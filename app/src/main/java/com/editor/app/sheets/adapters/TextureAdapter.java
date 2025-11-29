package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.editor.app.R;
import com.editor.app.api.models.Media;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TextureAdapter extends RecyclerView.Adapter<TextureAdapter.TextureViewHolder> {

    public interface OnTextureSelectedListener {
        void onTextureSelected(Media texture, int position);
    }

    private final List<Media> textures;
    private final OnTextureSelectedListener listener;
    private int selectedPosition = -1;

    public TextureAdapter(List<Media> textures, OnTextureSelectedListener listener) {
        this.textures = textures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TextureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_texture, parent, false);
        return new TextureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextureViewHolder holder, int position) {
        Media texture = textures.get(position);
        holder.bind(texture, position);
    }

    @Override
    public int getItemCount() {
        return textures.size();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;

        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(selectedPosition);
    }

    public void addTextures(List<Media> newTextures) {
        int startPosition = textures.size();
        textures.addAll(newTextures);
        notifyItemRangeInserted(startPosition, newTextures.size());
    }

    public class TextureViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView textureImage;

        TextureViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.textureCard);
            textureImage = itemView.findViewById(R.id.textureImage);
        }

        void bind(Media texture, int position) {
            // Load texture image from Unsplash using Glide
            Glide.with(itemView.getContext())
                    .load(texture.getUrls().getSmall())
                    .placeholder(R.drawable.image_24px)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(textureImage);

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
                    listener.onTextureSelected(texture, position);
                }
            });
        }
    }
}