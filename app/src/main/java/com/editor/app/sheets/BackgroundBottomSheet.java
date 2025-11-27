package com.editor.app.sheets;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.adapters.ColorAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

import java.util.Arrays;
import java.util.List;

public class BackgroundBottomSheet extends BaseBottomSheet {

    private OnBackgroundChangeListener listener;

    public interface OnBackgroundChangeListener {
        void onBackgroundColorChange(int color);
        void onBackgroundOpacityChange(float opacity);
        void onBorderSizeChange(float size);
        void onBorderColorChange(int color);
        void onBlurChange(float blur);
    }

    public void setOnBackgroundChangeListener(OnBackgroundChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void setupChips() {
        addChip("Edit", v -> showEditOptions());
        addChip("Border", v -> showBorderOptions());
        addChip("Opacity", v -> showOpacitySlider());
        addChip("Scale", v -> showScaleOptions());
        addChip("Blur", v -> showBlurSlider());
        addChip("Filter", v -> showFilterOptions());
        addChip("Effects", v -> showEffectsOptions());
        addChip("Blend", v -> showBlendOptions());

        selectFirstChip();
    }

    private void showEditOptions() {
        // Create sub-sheet with edit options
        View subSheet = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_base, null);

        ChipGroup subChipGroup = subSheet.findViewById(R.id.chipGroup);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);

        // Add sub-chips
        addSubChip(subChipGroup, subContent, "Color", this::showColorPicker);
        addSubChip(subChipGroup, subContent, "Gradient", this::showGradientPicker);
        addSubChip(subChipGroup, subContent, "Pattern", this::showPatternPicker);
        addSubChip(subChipGroup, subContent, "Gallery", this::showGallery);
        addSubChip(subChipGroup, subContent, "Camera", this::showCamera);

        // Select first sub-chip
        if (subChipGroup.getChildCount() > 0) {
            subChipGroup.getChildAt(0).performClick();
        }

        setContent(subSheet);
    }

    private void addSubChip(ChipGroup group, FrameLayout content, String label, Runnable action) {
        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(R.color.chip_unselected);
        chip.setTextColor(getResources().getColor(R.color.md_theme_onSurface, null));

        chip.setOnClickListener(v -> {
            // Update selection
            for (int i = 0; i < group.getChildCount(); i++) {
                com.google.android.material.chip.Chip c = (com.google.android.material.chip.Chip) group.getChildAt(i);
                if (c == chip) {
                    c.setChipBackgroundColorResource(R.color.chip_selected);
                    c.setTextColor(getResources().getColor(R.color.white, null));
                } else {
                    c.setChipBackgroundColorResource(R.color.chip_unselected);
                    c.setTextColor(getResources().getColor(R.color.md_theme_onSurface, null));
                }
            }

            // Update content
            content.removeAllViews();
            action.run();
        });

        group.addView(chip);
    }

    private void showColorPicker() {
        View colorPickerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_color_picker, null);

        RecyclerView recyclerView = colorPickerView.findViewById(R.id.colorRecyclerView);

        List<Integer> colors = getDefaultColors();
        ColorAdapter adapter = new ColorAdapter(colors, color -> {
            if (listener != null) {
                listener.onBackgroundColorChange(color);
            }
        });

        recyclerView.setAdapter(adapter);

        View subSheet = (View) contentContainer.getChildAt(0);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);
        subContent.removeAllViews();
        subContent.addView(colorPickerView);
    }

    private void showGradientPicker() {
        // TODO: Implement gradient picker
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Gradient Picker - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);

        View subSheet = (View) contentContainer.getChildAt(0);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);
        subContent.removeAllViews();
        subContent.addView(placeholder);
    }

    private void showPatternPicker() {
        // TODO: Implement pattern picker
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Pattern Picker - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);

        View subSheet = (View) contentContainer.getChildAt(0);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);
        subContent.removeAllViews();
        subContent.addView(placeholder);
    }

    private void showGallery() {
        // TODO: Implement gallery picker
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Gallery - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);

        View subSheet = (View) contentContainer.getChildAt(0);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);
        subContent.removeAllViews();
        subContent.addView(placeholder);
    }

    private void showCamera() {
        // TODO: Implement camera
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Camera - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);

        View subSheet = (View) contentContainer.getChildAt(0);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);
        subContent.removeAllViews();
        subContent.addView(placeholder);
    }

    private void showBorderOptions() {
        View subSheet = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_base, null);

        ChipGroup subChipGroup = subSheet.findViewById(R.id.chipGroup);
        FrameLayout subContent = subSheet.findViewById(R.id.contentContainer);

        addSubChip(subChipGroup, subContent, "Off", () -> showBorderOff(subContent));
        addSubChip(subChipGroup, subContent, "Size", () -> showBorderSize(subContent));
        addSubChip(subChipGroup, subContent, "Solid", () -> showBorderSolid(subContent));
        addSubChip(subChipGroup, subContent, "Gradient", () -> showBorderGradient(subContent));
        addSubChip(subChipGroup, subContent, "Pattern", () -> showBorderPattern(subContent));
        addSubChip(subChipGroup, subContent, "Opacity", () -> showBorderOpacity(subContent));

        if (subChipGroup.getChildCount() > 0) {
            subChipGroup.getChildAt(0).performClick();
        }

        setContent(subSheet);
    }

    private void showBorderOff(FrameLayout container) {
        if (listener != null) {
            listener.onBorderSizeChange(0);
        }

        TextView message = new TextView(requireContext());
        message.setText("Border turned off");
        message.setPadding(32, 32, 32, 32);
        container.removeAllViews();
        container.addView(message);
    }

    private void showBorderSize(FrameLayout container) {
        View sliderView = createSlider("Border Size", 0, 50, 5, value -> {
            if (listener != null) {
                listener.onBorderSizeChange(value);
            }
        });

        container.removeAllViews();
        container.addView(sliderView);
    }

    private void showBorderSolid(FrameLayout container) {
        View colorPickerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_color_picker, null);

        RecyclerView recyclerView = colorPickerView.findViewById(R.id.colorRecyclerView);

        List<Integer> colors = getDefaultColors();
        ColorAdapter adapter = new ColorAdapter(colors, color -> {
            if (listener != null) {
                listener.onBorderColorChange(color);
                listener.onBorderSizeChange(5); // Turn on border with default size
            }
        });

        recyclerView.setAdapter(adapter);
        container.removeAllViews();
        container.addView(colorPickerView);
    }

    private void showBorderGradient(FrameLayout container) {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Border Gradient - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        container.removeAllViews();
        container.addView(placeholder);
    }

    private void showBorderPattern(FrameLayout container) {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Border Pattern - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        container.removeAllViews();
        container.addView(placeholder);
    }

    private void showBorderOpacity(FrameLayout container) {
        View sliderView = createSlider("Border Opacity", 0, 100, 100, value -> {
            // TODO: Implement border opacity
        });

        container.removeAllViews();
        container.addView(sliderView);
    }

    private void showOpacitySlider() {
        View sliderView = createSlider("Background Opacity", 0, 100, 100, value -> {
            if (listener != null) {
                listener.onBackgroundOpacityChange(value / 100f);
            }
        });

        setContent(sliderView);
    }

    private void showScaleOptions() {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Scale Options - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        setContent(placeholder);
    }

    private void showBlurSlider() {
        View sliderView = createSlider("Blur", 0, 25, 0, value -> {
            if (listener != null) {
                listener.onBlurChange(value);
            }
        });

        setContent(sliderView);
    }

    private void showFilterOptions() {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Filter Options - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        setContent(placeholder);
    }

    private void showEffectsOptions() {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Effects Options - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        setContent(placeholder);
    }

    private void showBlendOptions() {
        showColorPicker(); // Similar to color picker for now
    }

    private View createSlider(String label, float min, float max, float initial, OnSliderChangeListener listener) {
        View sliderView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_slider, null);

        TextView labelView = sliderView.findViewById(R.id.sliderLabel);
        TextView valueView = sliderView.findViewById(R.id.sliderValue);
        Slider slider = sliderView.findViewById(R.id.slider);

        labelView.setText(label);
        slider.setValueFrom(min);
        slider.setValueTo(max);
        slider.setValue(initial);
        valueView.setText(String.valueOf((int) initial));

        slider.addOnChangeListener((sl, value, fromUser) -> {
            valueView.setText(String.valueOf((int) value));
            if (listener != null) {
                listener.onValueChange(value);
            }
        });

        return sliderView;
    }

    private List<Integer> getDefaultColors() {
        return Arrays.asList(
                Color.parseColor("#FFFFFF"), Color.parseColor("#000000"),
                Color.parseColor("#FF0000"), Color.parseColor("#00FF00"),
                Color.parseColor("#0000FF"), Color.parseColor("#FFFF00"),
                Color.parseColor("#FF00FF"), Color.parseColor("#00FFFF"),
                Color.parseColor("#FFA500"), Color.parseColor("#800080"),
                Color.parseColor("#FFC0CB"), Color.parseColor("#808080"),
                Color.parseColor("#A52A2A"), Color.parseColor("#FFD700"),
                Color.parseColor("#4B0082"), Color.parseColor("#00CED1"),
                Color.parseColor("#DC143C"), Color.parseColor("#32CD32")
        );
    }

    interface OnSliderChangeListener {
        void onValueChange(float value);
    }
}
