package com.editor.app.sheets.background;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.editor.app.R;
import com.google.android.material.slider.Slider;

public class OpacityOptionsView extends LinearLayout {

    public interface OpacityChangeListener {
        void onOpacityChanged(int opacity);
    }

    private OpacityChangeListener listener;
    private Slider opacitySlider;

    public OpacityOptionsView(Context context) {
        super(context);
        init(context);
    }

    public OpacityOptionsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_opacity_options, this, true);

        // Set layout params to match parent
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        setupViews();
    }

    private void setupViews() {
        opacitySlider = findViewById(R.id.opacitySlider);

        opacitySlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser && listener != null) {
                listener.onOpacityChanged((int) value);
            }
        });
    }

    public void setListener(OpacityChangeListener listener) {
        this.listener = listener;
    }

    public void setOpacity(int opacity) {
        opacitySlider.setValue(opacity);
    }

    public int getOpacity() {
        return (int) opacitySlider.getValue();
    }
}