package com.editor.app.sheets.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.editor.app.R;
import com.editor.app.api.models.Media;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TextureViewBinder {
    public interface OnTextureSelectedListener {
        void onTextureSelected(Media texture, int position);
    }
    private final List<Media> textures;
    private final OnTextureSelectedListener listener;
    private final LinearLayout container;
    private int selectedPosition = -1;
    private final List<ViewHolder> viewHolders = new ArrayList<>();

    public TextureViewBinder(LinearLayout container, List<Media> textures, OnTextureSelectedListener listener) {
        this.container = container;
        this.textures = textures;
        this.listener = listener;
        bindTextures();
    }

    private void bindTextures() {
        container.removeAllViews();
        viewHolders.clear();

        for (int i = 0; i < textures.size(); i++) {
            Media texture = textures.get(i);
            int position = i;

            // Inflate the texture item view
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_texture, container, false);

            ViewHolder holder = new ViewHolder(view, position);
            viewHolders.add(holder);
            holder.bind(texture);

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

    public void addTextures(List<Media> newTextures) {
        int startPosition = textures.size();
        textures.addAll(newTextures);

        // Bind only the new textures
        for (int i = startPosition; i < textures.size(); i++) {
            Media texture = textures.get(i);
            int position = i;

            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_texture, container, false);

            ViewHolder holder = new ViewHolder(view, position);
            viewHolders.add(holder);
            holder.bind(texture);

            container.addView(view);
        }
    }

    private class ViewHolder {
        MaterialCardView cardView;
        ImageView textureImage;
        Media texture;
        int position;

        ViewHolder(View itemView, int position) {
            this.position = position;
            cardView = itemView.findViewById(R.id.textureCard);
            textureImage = itemView.findViewById(R.id.textureImage);
        }

        void bind(Media item) {
            this.texture = item;

            // Load texture image from Unsplash using Glide
            Glide.with(cardView.getContext())
                    .load(texture.getUrls().getSmall())
                    .placeholder(R.drawable.image_24px)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(textureImage);

            // Update selection state
            updateSelection(position == selectedPosition);

            // Click listener
            cardView.setOnClickListener(v -> {
                setSelectedPosition(position);
                if (listener != null) {
                    listener.onTextureSelected(texture, position);
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