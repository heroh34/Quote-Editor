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
import com.editor.app.sheets.EditTextOptionsSheet;
import com.editor.app.sheets.ImageEditBottomSheet;
import com.editor.app.sheets.models.GradientItem;

public class MainActivity extends AppCompatActivity implements ImageEditBottomSheet.ImageEditListener,
        BackgroundEditBottomSheet.EditOptionsListener, EditTextOptionsSheet.TextEditListener {

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

    }

    private void showBackgroundEditOptionsBottomSheet() {
        BackgroundEditBottomSheet bottomSheet = BackgroundEditBottomSheet.newInstance();
        bottomSheet.setListener(this);
        bottomSheet.show(getSupportFragmentManager(), BackgroundEditBottomSheet.TAG);
    }

    private void showTextEditOptionsSheet() {
        EditTextOptionsSheet bottomSheet = EditTextOptionsSheet.newInstance(EditTextOptionsSheet.TextEditMode.EDIT);
        bottomSheet.show(getSupportFragmentManager(), BackgroundEditBottomSheet.TAG);
    }

    private void showImageOptionsBottomSheet() {
        ImageEditBottomSheet bottomSheet = ImageEditBottomSheet.newInstance(ImageEditBottomSheet.ImageEditMode.EDIT);
        bottomSheet.setListener(this);
        bottomSheet.show(getSupportFragmentManager(), ImageEditBottomSheet.TAG);
    }

    // IMAGE EDIT OPTIONS AND METHODS

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
    public void onColorSelected(int color) {

    }

    @Override
    public void onColorGradientSelected(GradientItem gradient) {
//        public class GradientItem {
//            private int[] colors; // Array of colors for gradient
//            private String name;
//            private boolean isSelected;
//
//            // Getters and Setters
//        }
    }

    @Override
    public void onRemoveColorClicked() {

    }

    @Override
    public void onRemoveGradientClicked() {

    }

    @Override
    public void onShadowAngleChanged(String direction) { // RIGHT, DOWN, UP, LEFT

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

    @Override
    public void onPositionChanged(ImageEditBottomSheet.ImagePosition imagePosition) {
//        public enum ImagePosition {
//            VERTICAL_TOP, VERTICAL_BOTTOM, VERTICAL_CENTER,
//            HORIZONTAL_LEFT, HORIZONTAL_CENTER, HORIZONTAL_RIGHT
//        }
    }

    @Override
    public void onZRotationChanged(float degrees) {

    }

    @Override
    public void onXRotationChanged(float degrees) {

    }

    @Override
    public void onYRotationChanged(float degrees) {

    }

    @Override
    public void onFlipHorizontal() {

    }

    @Override
    public void onFlipVertical() {

    }

    // END

    // BACKGROUND IMAGE EDIT OPTIONS

    @Override
    public void onBorderStateChanged(boolean isOn, int size, int opacity) {

    }

    @Override
    public void onBorderTypeChanged(BackgroundEditBottomSheet.BorderType type, int color) {
//        public enum BorderType {
//            OFF, SOLID, GRADIENT, PATTERN
//        }
    }

    @Override
    public void onSolidColorSelected(int color) {

    }

    @Override
    public void onGradientSelected(GradientItem gradient) {

    }

    @Override
    public void onTextureSelected(Media texture) {
//        public class Media implements Serializable {
//            private String id;
//            private String slug;
//            private String color;
//            private Urls urls;
//
//            public static class Urls implements Serializable {
//                private String raw;
//                private String full;
//                private String regular;
//                private String small;
//                private String thumb;
//                private String small_s3;
//
//                // Getters
//
//                public String getRaw() {
//                    return raw;
//                }
//
//                public String getFull() {
//                    return full;
//                }
//
//                public String getRegular() {
//                    return regular;
//                }
//
//                public String getSmall() {
//                    return small;
//                }
//
//                public String getThumb() {
//                    return thumb;
//                }
//
//                public String getSmall_s3() {
//                    return small_s3;
//                }
//            }
//
//            // Getters
//
//        }
    }

    @Override
    public void onOpacityChanged(int opacity) {

    }

    @Override
    public void onScaleChanged(int scale) {

    }

    @Override
    public void onScaleTypeChanged(BackgroundEditBottomSheet.ScaleType scaleType) {
//        public enum ScaleType {
//            ASPECT_FILL, CENTER, TOP, BOTTOM, LEFT, RIGHT, FIT
//        }
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
    public void onBlurChanged(int blurRadius) {

    }

    @Override
    public void onBlendColorSelected(int color) {

    }

    @Override
    public void onRemoveBlendClicked() {

    }

    // END

    // TEXT EDIT OPTIONS

    @Override
    public void onEditModeChanged(boolean isEditMode) {

    }

    @Override
    public void onCanvasPositionChanged(String direction) {

    }

    @Override
    public void onTextColorSelected(int color) {

    }

    @Override
    public void onTextGradientSelected(GradientItem gradient) {

    }

    @Override
    public void onTextPatternSelected(Media pattern) {

    }

    @Override
    public void onRemoveTextColorClicked() {

    }

    @Override
    public void onTextSizeChanged(int size) {

    }

    @Override
    public void onFontSelected(String fontName) {

    }

    @Override
    public void onSeeAllFontsClicked() {

    }

    @Override
    public void onTextShadowAngleChanged(String direction) { // RIGHT, DOWN, UP, LEFT

    }

    @Override
    public void onTextShadowBlurChanged(int blur) {

    }

    @Override
    public void onTextShadowColorSelected(int color) {

    }

    @Override
    public void onTextShadowOpacityChanged(int opacity) {

    }

    @Override
    public void onTextShadowOff() {

    }

    @Override
    public void onTextPositionChanged(EditTextOptionsSheet.TextPosition position) {
//        public enum TextPosition {
//            VERTICAL_TOP, VERTICAL_BOTTOM, VERTICAL_CENTER,
//            HORIZONTAL_LEFT, HORIZONTAL_CENTER, HORIZONTAL_RIGHT
//        }
    }

    @Override
    public void onTextAlignmentChanged(EditTextOptionsSheet.TextAlignment alignment) {
//        public enum TextAlignment {
//            LEFT, CENTER, RIGHT, JUSTIFY
//        }
    }

    @Override
    public void onTextCaseChanged(EditTextOptionsSheet.TextCase textCase) {
//        public enum TextCase {
//            NORMAL, UPPERCASE, LOWERCASE, CAPITALIZE
//        }
    }

    @Override
    public void onBoldToggled(boolean isBold) {

    }

    @Override
    public void onItalicToggled(boolean isItalic) {

    }

    @Override
    public void onUnderlineToggled(boolean isUnderline) {

    }

    @Override
    public void onTextRotationChanged(float degrees) {

    }

    @Override
    public void onTextCurveChanged(int curveAmount) {

    }

    @Override
    public void onAutoParagraphChanged(int paragraphWidth) {

    }

    @Override
    public void onLineSpaceChanged(float lineSpacing) {

    }

    @Override
    public void onLetterSpaceChanged(float letterSpacing) {

    }

    @Override
    public void onTextOpacityChanged(int opacity) {

    }
}