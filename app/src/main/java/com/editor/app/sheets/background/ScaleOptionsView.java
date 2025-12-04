package com.editor.app.sheets.background;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.editor.app.R;
import com.editor.app.sheets.BackgroundEditBottomSheet.ScaleType;
import com.google.android.material.card.MaterialCardView;

public class ScaleOptionsView extends LinearLayout {

    public interface ScaleOptionsViewListener {
        void onScaleChanged(int scale);
        void onScaleTypeChanged(ScaleType scaleType);
    }

    private ScaleOptionsViewListener listener;
    private LinearLayout scaleTypesContainer;
    private ScaleType currentScaleType = ScaleType.ASPECT_FILL;

    public ScaleOptionsView(Context context) {
        super(context);
        init(context);
    }

    public ScaleOptionsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_scale_options, this, true);
        // Set layout params to match parent
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        setupViews();
    }

    private void setupViews() {
        scaleTypesContainer = findViewById(R.id.scaleTypesContainer);
        // Setup scale type buttons
        setupScaleTypeButtons();
    }

    private void setupScaleTypeButtons() {
        ScaleType[] scaleTypes = {
                ScaleType.ASPECT_FILL,
                ScaleType.CENTER,
                ScaleType.TOP,
                ScaleType.BOTTOM,
                ScaleType.LEFT,
                ScaleType.RIGHT,
                ScaleType.FIT
        };

        String[] scaleLabels = {
                "Aspect Fill",
                "Center",
                "Top",
                "Bottom",
                "Left",
                "Right",
                "Fit"
        };

        int[] scaleIcons = {
                R.drawable.fullscreen_24px,
                R.drawable.center_focus_strong_24px,
                R.drawable.toolbar_24px,
                R.drawable.bottom_navigation_24px,
                R.drawable.left_panel_close_24px,
                R.drawable.right_panel_close_24px,
                R.drawable.fit_page_width_24px
        };

        for (int i = 0; i < scaleTypes.length; i++) {
            final ScaleType type = scaleTypes[i];
            View scaleTypeView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_scale_type, scaleTypesContainer, false);

            MaterialCardView cardView = scaleTypeView.findViewById(R.id.scaleTypeCard);
            ImageView iconView = scaleTypeView.findViewById(R.id.scaleTypeIcon);
            TextView labelView = scaleTypeView.findViewById(R.id.scaleTypeLabel);

            iconView.setImageResource(scaleIcons[i]);
            labelView.setText(scaleLabels[i]);

            cardView.setOnClickListener(v -> {
                currentScaleType = type;
                if (listener != null) {
                    listener.onScaleTypeChanged(type);
                }
                highlightSelectedType(cardView);
            });

            scaleTypesContainer.addView(scaleTypeView);
        }
    }

    private void highlightSelectedType(MaterialCardView selectedCard) {
        // Reset all cards
        for (int i = 0; i < scaleTypesContainer.getChildCount(); i++) {
            View child = scaleTypesContainer.getChildAt(i);
            MaterialCardView card = child.findViewById(R.id.scaleTypeCard);
            if (card != null) {
                card.setCardElevation(2f);
                card.setStrokeWidth(0);
            }
        }

        // Highlight selected
        selectedCard.setCardElevation(8f);
        selectedCard.setStrokeWidth(3);
        selectedCard.setStrokeColor(getContext().getColor(R.color.md_theme_primary));
    }

    public void setListener(ScaleOptionsViewListener listener) {
        this.listener = listener;
    }
}