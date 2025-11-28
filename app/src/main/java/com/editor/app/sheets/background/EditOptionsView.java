package com.editor.app.sheets.background;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import com.editor.app.R;

public class EditOptionsView extends LinearLayout {

    public interface EditOptionsViewListener {
        void onColorClicked();
        void onGradientClicked();
        void onPatternClicked();
        void onGalleryClicked();
        void onCameraClicked();
    }

    private EditOptionsViewListener listener;

    public EditOptionsView(Context context) {
        super(context);
        init(context);
    }

    public EditOptionsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_edit_options, this, true);

        // Set layout params to match parent
        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.btnColor).setOnClickListener(v -> {
            if (listener != null) listener.onColorClicked();
        });

        findViewById(R.id.btnGradient).setOnClickListener(v -> {
            if (listener != null) listener.onGradientClicked();
        });

        findViewById(R.id.btnPattern).setOnClickListener(v -> {
            if (listener != null) listener.onPatternClicked();
        });

        findViewById(R.id.btnGallery).setOnClickListener(v -> {
            if (listener != null) listener.onGalleryClicked();
        });

        findViewById(R.id.btnCamera).setOnClickListener(v -> {
            if (listener != null) listener.onCameraClicked();
        });
    }

    public void setListener(EditOptionsViewListener listener) {
        this.listener = listener;
    }
}
