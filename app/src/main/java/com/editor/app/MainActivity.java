package com.editor.app;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.editor.app.sheets.BackgroundEditBottomSheet;

public class MainActivity extends AppCompatActivity implements BackgroundEditBottomSheet.EditOptionsListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        ImageButton btnShowEditOptions = findViewById(R.id.background_edit_options);
        btnShowEditOptions.setOnClickListener(v -> showEditOptionsBottomSheet());
    }

    private void showEditOptionsBottomSheet() {
        BackgroundEditBottomSheet bottomSheet = BackgroundEditBottomSheet.newInstance();
        bottomSheet.setListener(this);
        bottomSheet.show(getSupportFragmentManager(), BackgroundEditBottomSheet.TAG);
    }

    @Override
    public void onBorderStateChanged(boolean isOn, int size, int opacity) {

    }

    @Override
    public void onBorderTypeChanged(BackgroundEditBottomSheet.BorderType type, int color) {

    }

    @Override
    public void onOpacityChanged(int opacity) {

    }

    @Override
    public void onScaleChanged(int scale) {

    }

    @Override
    public void onScaleTypeChanged(BackgroundEditBottomSheet.ScaleType scaleType) {

    }

    @Override
    public void onColorClicked() {

    }

    @Override
    public void onGradientClicked() {

    }

    @Override
    public void onPatternClicked() {

    }

    @Override
    public void onGalleryClicked() {

    }

    @Override
    public void onCameraClicked() {

    }
}