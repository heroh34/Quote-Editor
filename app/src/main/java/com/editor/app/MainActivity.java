package com.editor.app;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.editor.app.api.models.Media;
import com.editor.app.sheets.BackgroundEditBottomSheet;
import com.editor.app.sheets.ImageEditBottomSheet;
import com.editor.app.sheets.models.GradientItem;

public class MainActivity extends AppCompatActivity implements BackgroundEditBottomSheet.EditOptionsListener,
ImageEditBottomSheet.ImageEditListener {

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
        btnShowEditOptions.setOnClickListener(v -> showImageOptionsBottomSheet());
    }

    private void showEditOptionsBottomSheet() {
        BackgroundEditBottomSheet bottomSheet = BackgroundEditBottomSheet.newInstance();
        bottomSheet.setListener(this);
        bottomSheet.show(getSupportFragmentManager(), BackgroundEditBottomSheet.TAG);
    }

    private void showImageOptionsBottomSheet() {
        ImageEditBottomSheet bottomSheet = ImageEditBottomSheet.newInstance(ImageEditBottomSheet.ImageEditMode.EDIT);
        bottomSheet.setListener(this);
        bottomSheet.show(getSupportFragmentManager(), ImageEditBottomSheet.TAG);
    }

    @Override
    public void onBorderStateChanged(boolean isOn, int size, int opacity) {

    }

    @Override
    public void onBorderTypeChanged(BackgroundEditBottomSheet.BorderType type, int color) {

    }

    @Override
    public void onSolidColorSelected(int color) {

    }

    @Override
    public void onGradientSelected(GradientItem gradient) {

    }

    @Override
    public void onTextureSelected(Media texture) {

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
    public void onBackgroundColorSelected(int color) {

    }

    @Override
    public void onBackgroundGradientSelected(GradientItem gradient) {

    }

    @Override
    public void onBackgroundTextureSelected(Media texture) {

    }

    @Override
    public void onGalleryClicked() {

    }

    @Override
    public void onCameraClicked() {

    }

    @Override
    public void onReplaceClicked() {

    }

    @Override
    public void onEraseClicked() {

    }

    @Override
    public void onSizeChanged(int size) {

    }

    @Override
    public void onAspectCropClicked() {

    }

    @Override
    public void onShapeCropClicked() {

    }

    @Override
    public void onAIRemoveBackground() {

    }

    @Override
    public void onColorSelected(int color) {

    }

    @Override
    public void onColorGradientSelected(GradientItem gradient) {

    }

    @Override
    public void onRemoveColorClicked() {

    }

    @Override
    public void onEffectSelected(String effectName) {

    }

    @Override
    public void onSeeAllEffectsClicked() {

    }

    @Override
    public void onHueChanged(int hue) {

    }

    @Override
    public void onShadowAngleChanged(String direction) {

    }

    @Override
    public void onShadowBlurChanged(int blur) {

    }

    @Override
    public void onShadowColorSelected(int color) {

    }

    @Override
    public void onShadowOpacityChanged(int opacity) {

    }

    @Override
    public void onShadowOff() {

    }
}