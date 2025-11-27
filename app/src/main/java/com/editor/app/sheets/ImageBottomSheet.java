package com.editor.app.sheets;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.adapters.ColorAdapter;
import com.google.android.material.slider.Slider;

import java.util.Arrays;
import java.util.List;

public class ImageBottomSheet extends BaseBottomSheet {

    private OnImageChangeListener listener;

    public interface OnImageChangeListener {
        void onImageSizeChange(float size);
        void onImageColorChange(int color);
    }

    public void setOnImageChangeListener(OnImageChangeListener listener) {
        this.listener = listener;
    }

    @Override
    protected void setupChips() {
        addChip("Edit", v -> showEditOptions());
        addChip("Size", v -> showSizeSlider());
        addChip("Crop", v -> showCropOptions());
        addChip("Color", v -> showColorOptions());

        selectFirstChip();
    }

    private void showEditOptions() {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Image Edit Options - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        setContent(placeholder);
    }

    private void showSizeSlider() {
        View sliderView = createSlider("Image Size", 10, 200, 100, value -> {
            if (listener != null) {
                listener.onImageSizeChange(value);
            }
        });

        setContent(sliderView);
    }

    private void showCropOptions() {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Crop Options - Coming Soon");
        placeholder.setPadding(32, 32, 32, 32);
        setContent(placeholder);
    }

    private void showColorOptions() {
        View colorPickerView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_color_picker, null);

        RecyclerView recyclerView = colorPickerView.findViewById(R.id.colorRecyclerView);

        List<Integer> colors = getDefaultColors();
        ColorAdapter adapter = new ColorAdapter(colors, color -> {
            if (listener != null) {
                listener.onImageColorChange(color);
            }
        });

        recyclerView.setAdapter(adapter);

        setContent(colorPickerView);
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
                Color.parseColor("#FFC0CB"), Color.parseColor("#808080")
        );
    }

    interface OnSliderChangeListener {
        void onValueChange(float value);
    }
}
