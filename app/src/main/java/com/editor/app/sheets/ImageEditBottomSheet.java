package com.editor.app.sheets;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.editor.app.R;
import com.editor.app.sheets.adapters.ColorViewBinder;
import com.editor.app.sheets.adapters.GradientViewBinder;
import com.editor.app.sheets.models.ColorItem;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class ImageEditBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "ImageEditBottomSheet";
    private static final String ARG_SELECTED_MODE = "selected_mode";

    // Listener interface
    public interface ImageEditListener {
        // Edit mode
        void onReplaceClicked();
        void onEraseClicked();

        // Size mode
        void onSizeChanged(int size);

        // Crop mode
        void onAspectCropClicked();
        void onShapeCropClicked();

        // Color mode
        void onColorSelected(int color);
        void onColorGradientSelected(GradientItem gradient);
        void onRemoveColorClicked();
        void onRemoveGradientClicked();

        // Shadow mode
        void onShadowAngleChanged(String direction); // LEFT, UP, DOWN, RIGHT
        void onShadowBlurChanged(int blur);
        void onShadowColorSelected(int color);
        void onShadowOpacityChanged(int opacity);
        void onShadowOff();

        // Position Mode
        void onPositionChanged(ImagePosition imagePosition);

        // 3D Rotation mode
        void onZRotationChanged(float degrees);
        void onXRotationChanged(float degrees);
        void onYRotationChanged(float degrees);
        void onFlipHorizontal();
        void onFlipVertical();

        // Opacity mode
        void onOpacityChanged(int opacity);
    }

    // Enums
    public enum ImageEditMode {
        EDIT, SIZE, CROP, COLOR, EFFECTS, HUE, SHADOW,
        POSITION, ROTATION, OPACITY
    }

    public enum ImagePosition {
        VERTICAL_TOP, VERTICAL_BOTTOM, VERTICAL_CENTER,
        HORIZONTAL_LEFT, HORIZONTAL_CENTER, HORIZONTAL_RIGHT
    }

    private ImageEditListener listener;
    private ChipGroup chipGroup;
    private FrameLayout contentContainer;
    private ImageButton backButton;
    private ImageEditMode currentMode = ImageEditMode.EDIT;

    // Content views (cached)
    private FrameLayout shadowContentContainer;
    private View editContent, sizeContent, cropContent, colorContent, effectsContent, hueContent,
            shadowContent, positionContent, rotation3dContent, opacityContent;
    private View shadowColorContent, colorSolidContent, colorGradientContent, shadowOpacityContent, shadowBlurContent;

    // DATA
    private int currentColor = Color.BLACK;
    private GradientItem currentGradient;

    public static ImageEditBottomSheet newInstance(ImageEditMode selectedMode) {
        ImageEditBottomSheet sheet = new ImageEditBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_MODE, selectedMode.name());
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(ImageEditListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_edit_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipGroup = view.findViewById(R.id.imageEditChipGroup);
        contentContainer = view.findViewById(R.id.imageEditContentContainer);
        backButton = view.findViewById(R.id.backButton);

        // Get selected mode from arguments
        if (getArguments() != null) {
            String modeName = getArguments().getString(ARG_SELECTED_MODE);
            if (modeName != null) {
                currentMode = ImageEditMode.valueOf(modeName);
            }
        }

        setupBackButton();
        setupDynamicChips();
        showSelectedMode();
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> dismiss());
    }

    private void setupDynamicChips() {
        String[] chipTitles = {"Edit", "Size", "Crop", "Color", "Shadow",
                "Position", "Rotation", "Opacity"};
        ImageEditMode[] modes = {
                ImageEditMode.EDIT,
                ImageEditMode.SIZE,
                ImageEditMode.CROP,
                ImageEditMode.COLOR,
                ImageEditMode.SHADOW,
                ImageEditMode.POSITION,
                ImageEditMode.ROTATION,
                ImageEditMode.OPACITY
        };

        for (int i = 0; i < chipTitles.length; i++) {
            Chip chip = (Chip) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filter_chip, chipGroup, false);

            chip.setText(chipTitles[i]);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            final ImageEditMode mode = modes[i];

            // Set checked if this is the selected mode
            if (mode == currentMode) {
                chip.setChecked(true);
            }

            chip.setOnClickListener(v -> onChipSelected(mode));
            chipGroup.addView(chip);
        }
    }

    private void showSelectedMode() {
        onChipSelected(currentMode);
    }

    private void onChipSelected(ImageEditMode mode) {
        currentMode = mode;
        contentContainer.removeAllViews();

        switch (mode) {
            case EDIT:
                showEditContent();
                break;
            case SIZE:
                showSizeContent();
                break;
            case CROP:
                showCropContent();
                break;
            case COLOR:
                showColorContent();
                break;
            case SHADOW:
                showShadowContent();
                break;
            case POSITION:
                showPositionContent();
                break;
            case ROTATION:
                show3DRotationContent();
                break;
            case OPACITY:
                showOpacityContent();
                break;
        }
    }

    // ============================================================================
    // EDIT MODE
    // ============================================================================

    private void showEditContent() {
        if (editContent == null) {
            editContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_edit_content, contentContainer, false);
            setupEditButtons();
        }
        contentContainer.addView(editContent);
    }

    private void setupEditButtons() {
        MaterialButton replaceButton = editContent.findViewById(R.id.replaceButton);
        MaterialButton eraseButton = editContent.findViewById(R.id.eraseButton);

        replaceButton.setOnClickListener(v -> {
            if (listener != null) listener.onReplaceClicked();
        });

        eraseButton.setOnClickListener(v -> {
            if (listener != null) listener.onEraseClicked();
        });
    }

    // ============================================================================
    // SIZE MODE
    // ============================================================================

    private void showSizeContent() {
        if (sizeContent == null) {
            sizeContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_size, contentContainer, false);
            setupSizeSlider();
        }
        contentContainer.addView(sizeContent);
    }

    private void setupSizeSlider() {
        TextView sizeValueText = sizeContent.findViewById(R.id.sizeValue);
        Slider sizeSlider = sizeContent.findViewById(R.id.sizeSlider);

        // Configure for blur (0-50)
        sizeSlider.setValueFrom(0);
        sizeSlider.setValueTo(1024);
        sizeSlider.setValue(0);
        // sizeSlider.setStepSize(5);

        sizeSlider.addOnChangeListener((slider, value, fromUser) -> {
            int sizeValue = (int) value;
            sizeValueText.setText(sizeValue + "px");
            if (listener != null) {
                listener.onSizeChanged(sizeValue);
            }
        });
    }

    // ============================================================================
    // CROP MODE
    // ============================================================================

    private void showCropContent() {
        if (cropContent == null) {
            cropContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_edit_content_crop, contentContainer, false);
            setupCropButtons();
        }
        contentContainer.addView(cropContent);
    }

    private void setupCropButtons() {
        MaterialButton aspectCropButton = cropContent.findViewById(R.id.aspectCropButton);
        MaterialButton shapeCropButton = cropContent.findViewById(R.id.shapeCropButton);

        aspectCropButton.setOnClickListener(v -> {
            if (listener != null) listener.onAspectCropClicked();
        });

        shapeCropButton.setOnClickListener(v -> {
            if (listener != null) listener.onShapeCropClicked();
        });
    }

    // ============================================================================
    // COLOR MODE
    // ============================================================================

    private void showColorContent() {
        if (colorContent == null) {
            colorContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_edit_content_color, contentContainer, false);
            setupColorTabs();
        }
        contentContainer.addView(colorContent);
    }

    private void setupColorTabs() {
        FrameLayout tabContentContainer = colorContent.findViewById(R.id.colorTabContentContainer);
        ChipGroup chipGroup = colorContent.findViewById(R.id.imageColorChipGroup);

        // Default: show solid
        showSolidColorContent(tabContentContainer);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int selectedId = checkedIds.get(0);

            if (selectedId == R.id.chipSolid) {
                showSolidColorContent(tabContentContainer);
            } else if (selectedId == R.id.chipGradient) {
                showGradientColorContent(tabContentContainer);
            }
        });
    }

    private void showSolidColorContent(FrameLayout container) {
        container.removeAllViews();
        if (colorSolidContent == null) {
            colorSolidContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_solid, container, false);
            setupSolidColorPickers();
        }
        container.addView(colorSolidContent);
    }

    private void setupSolidColorPickers() {
        MaterialCardView gradientPickerCard = colorSolidContent.findViewById(R.id.gradientPickerCard);
        MaterialCardView eyedropperCard = colorSolidContent.findViewById(R.id.eyedropperCard);
        MaterialCardView removeColorCard = colorSolidContent.findViewById(R.id.removeColorCard);

        removeColorCard.setVisibility(View.VISIBLE);

        List<ColorItem> colors = createColorList();
        LinearLayout colorsContainer = colorSolidContent.findViewById(R.id.colorsContainer);

        new ColorViewBinder(colorsContainer, colors, (color, position) -> {
            currentColor = color;
            if (listener != null) {
                listener.onColorSelected(color);
            }
        });

        removeColorCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveColorClicked();
            }
        });

        eyedropperCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Eyedropper coming soon!", Toast.LENGTH_SHORT).show();
        });

        gradientPickerCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Color picker coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showGradientColorContent(FrameLayout container) {
        container.removeAllViews();
        if (colorGradientContent == null) {
            colorGradientContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_gradient, container, false);
            setupGradientPickers(colorGradientContent);
        }
        container.addView(colorGradientContent);
    }

    private void setupGradientPickers(View contentView) {
        MaterialCardView gradientPickerCard = contentView.findViewById(R.id.gradientPickerCard);
        MaterialCardView removeGradientCard = contentView.findViewById(R.id.removeColorCard);

        removeGradientCard.setVisibility(View.VISIBLE);

        List<GradientItem> gradients = createGradientList();
        LinearLayout gradientsContainer = contentView.findViewById(R.id.gradientsContainer);

        new GradientViewBinder(gradientsContainer, gradients, (gradient, position) -> {
            currentGradient = gradient;
            if (listener != null) {
                listener.onColorGradientSelected(gradient);
            }
        });

        gradientPickerCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Gradient picker coming soon!", Toast.LENGTH_SHORT).show();
        });

        removeGradientCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveGradientClicked();
            }
        });
    }

    private void showShadowContent() {
        if (shadowContent == null) {
            shadowContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_edit_content_shadow, contentContainer, false);
            setupShadowSubOptions();
        }
        contentContainer.addView(shadowContent);
    }

    private void setupShadowSubOptions() {
        ChipGroup shadowChipGroup = shadowContent.findViewById(R.id.shadowSubOptionsChipGroup);
        shadowContentContainer = shadowContent.findViewById(R.id.shadowContentContainer);

        // Default: Angle selected
        showShadowAngleContent();

        shadowChipGroup.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int selectedId = checkedIds.get(0);

            if (selectedId == R.id.shadowOffChip) {
                shadowContentContainer.removeAllViews();
                if (listener != null) {
                    listener.onShadowOff();
                }
            } else if (selectedId == R.id.shadowAngleChip) {
                showShadowAngleContent();
            } else if (selectedId == R.id.shadowBlurChip) {
                showShadowBlurContent();
            } else if (selectedId == R.id.shadowColorChip) {
                showShadowColorContent();
            } else if (selectedId == R.id.shadowOpacityChip) {
                showShadowOpacityContent();
            }
        });
    }

    private void showShadowAngleContent() {
        shadowContentContainer.removeAllViews();
        View angleContent = LayoutInflater.from(getContext())
                .inflate(R.layout.shadow_content_angle, shadowContentContainer, false);

        MaterialButton leftButton = angleContent.findViewById(R.id.angleLeftButton);
        MaterialButton upButton = angleContent.findViewById(R.id.angleUpButton);
        MaterialButton downButton = angleContent.findViewById(R.id.angleDownButton);
        MaterialButton rightButton = angleContent.findViewById(R.id.angleRightButton);

        leftButton.setOnClickListener(v -> {
            if (listener != null) listener.onShadowAngleChanged("LEFT");
        });

        upButton.setOnClickListener(v -> {
            if (listener != null) listener.onShadowAngleChanged("UP");
        });

        downButton.setOnClickListener(v -> {
            if (listener != null) listener.onShadowAngleChanged("DOWN");
        });

        rightButton.setOnClickListener(v -> {
            if (listener != null) listener.onShadowAngleChanged("RIGHT");
        });

        shadowContentContainer.addView(angleContent);
    }

    private void showShadowBlurContent() {
        shadowContentContainer.removeAllViews();
        if (shadowBlurContent == null) {
            shadowBlurContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_size, shadowContentContainer, false);
            setupShadowBlurSlider();
        }
        shadowContentContainer.addView(shadowBlurContent);
    }

    private void setupShadowBlurSlider() {
        TextView valueText = shadowBlurContent.findViewById(R.id.sizeValue);
        Slider slider = shadowBlurContent.findViewById(R.id.sizeSlider);

        // Configure for blur (0-50)
        slider.setValueFrom(0);
        slider.setValueTo(50);
        slider.setValue(0);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int blurValue = (int) value;
            valueText.setText(blurValue + "px");
            if (listener != null) {
                listener.onShadowBlurChanged(blurValue);
            }
        });
    }

    private void showShadowColorContent() {
        shadowContentContainer.removeAllViews();
        if (shadowColorContent == null) {
            shadowColorContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_solid, shadowContentContainer, false);
            setupShadowColorPickers();
        }
        shadowContentContainer.addView(shadowColorContent);
    }

    private void setupShadowColorPickers() {
        MaterialCardView gradientPickerCard = shadowColorContent.findViewById(R.id.gradientPickerCard);
        MaterialCardView eyedropperCard = shadowColorContent.findViewById(R.id.eyedropperCard);
        MaterialCardView removeCard = shadowColorContent.findViewById(R.id.removeColorCard);

        // Hide remove card for shadow color
        removeCard.setVisibility(View.GONE);

        List<ColorItem> colors = createColorList();
        LinearLayout colorsContainer = shadowColorContent.findViewById(R.id.colorsContainer);

        new ColorViewBinder(colorsContainer, colors, (color, position) -> {
            if (listener != null) {
                listener.onShadowColorSelected(color);
            }
        });

        gradientPickerCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Color picker coming soon!", Toast.LENGTH_SHORT).show();
        });

        eyedropperCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Eyedropper coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showShadowOpacityContent() {
        shadowContentContainer.removeAllViews();
        if (shadowOpacityContent == null) {
            shadowOpacityContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_opacity, shadowContentContainer, false);
            setupShadowOpacitySlider();
        }
        shadowContentContainer.addView(shadowOpacityContent);
    }

    private void setupShadowOpacitySlider() {
        TextView valueText = shadowOpacityContent.findViewById(R.id.opacityValue);
        Slider slider = shadowOpacityContent.findViewById(R.id.opacitySlider);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int opacityValue = (int) value;
            valueText.setText(opacityValue + "%");
            if (listener != null) {
                listener.onShadowOpacityChanged(opacityValue);
            }
        });
    }

    // ============================================================================
    // POSITION MODE
    // ============================================================================

    private void showPositionContent() {
        if (positionContent == null) {
            positionContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.position_content, contentContainer, false);
            setupPositionButtons();
        }
        contentContainer.addView(positionContent);
    }

    private void setupPositionButtons() {
        MaterialButton verticalTopBtn = positionContent.findViewById(R.id.vertical_top_btn);
        MaterialButton verticalCenterBtn = positionContent.findViewById(R.id.vertical_center_btn);
        MaterialButton verticalBottomBtn = positionContent.findViewById(R.id.vertical_bottom_btn);
        MaterialButton horizCenterBtn = positionContent.findViewById(R.id.horizontal_center_btn);
        MaterialButton horizRightBtn = positionContent.findViewById(R.id.horizontal_right_btn);
        MaterialButton horizLeftBtn = positionContent.findViewById(R.id.horizontal_left_btn);

        verticalTopBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPositionChanged(ImagePosition.VERTICAL_TOP);
        });

        verticalCenterBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPositionChanged(ImagePosition.VERTICAL_CENTER);
        });

        verticalBottomBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPositionChanged(ImagePosition.VERTICAL_BOTTOM);
        });

        horizCenterBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPositionChanged(ImagePosition.HORIZONTAL_CENTER);
        });

        horizRightBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPositionChanged(ImagePosition.HORIZONTAL_RIGHT);
        });

        horizLeftBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPositionChanged(ImagePosition.HORIZONTAL_LEFT);
        });
    }

    // ============================================================================
    // 3D ROTATION MODE
    // ============================================================================

    private void show3DRotationContent() {
        if (rotation3dContent == null) {
            rotation3dContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_edit_content_3d_rotation, contentContainer, false);
            setup3DRotationSubOptions();
        }
        contentContainer.addView(rotation3dContent);
    }

    private void setup3DRotationSubOptions() {
        Chip zRotationChip = rotation3dContent.findViewById(R.id.zRotationChip);
        Chip xRotationChip = rotation3dContent.findViewById(R.id.xRotationChip);
        Chip yRotationChip = rotation3dContent.findViewById(R.id.yRotationChip);
        Chip flipChip = rotation3dContent.findViewById(R.id.flipChip);
        FrameLayout rotation3dContentContainer = rotation3dContent.findViewById(R.id.rotation3dContentContainer);

        // Default: Z-Rotation selected
        zRotationChip.setChecked(true);
        showZRotationContent(rotation3dContentContainer);

        zRotationChip.setOnClickListener(v -> showZRotationContent(rotation3dContentContainer));
        xRotationChip.setOnClickListener(v -> showXRotationContent(rotation3dContentContainer));
        yRotationChip.setOnClickListener(v -> showYRotationContent(rotation3dContentContainer));
        flipChip.setOnClickListener(v -> showFlipContent(rotation3dContentContainer));
    }

    private void showZRotationContent(FrameLayout container) {
        container.removeAllViews();
        View zRotationContent = LayoutInflater.from(getContext())
                .inflate(R.layout.rotation_content_z, container, false);

        TextView valueText = zRotationContent.findViewById(R.id.zRotationValueText);
        Slider slider = zRotationContent.findViewById(R.id.zRotationSlider);
        MaterialButton resetButton = zRotationContent.findViewById(R.id.zRotationResetButton);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            valueText.setText(String.format("%.0f°", value));
            if (listener != null) {
                listener.onZRotationChanged(value);
            }
        });

        resetButton.setOnClickListener(v -> {
            slider.setValue(0);
            valueText.setText("0°");
            if (listener != null) {
                listener.onZRotationChanged(0);
            }
        });

        container.addView(zRotationContent);
    }

    private void showXRotationContent(FrameLayout container) {
        container.removeAllViews();
        View xRotationContent = LayoutInflater.from(getContext())
                .inflate(R.layout.rotation_content_x, container, false);

        TextView valueText = xRotationContent.findViewById(R.id.xRotationValueText);
        Slider slider = xRotationContent.findViewById(R.id.xRotationSlider);
        MaterialButton resetButton = xRotationContent.findViewById(R.id.xRotationResetButton);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            valueText.setText(String.format("%.0f°", value));
            if (listener != null) {
                listener.onXRotationChanged(value);
            }
        });

        resetButton.setOnClickListener(v -> {
            slider.setValue(0);
            valueText.setText("0°");
            if (listener != null) {
                listener.onXRotationChanged(0);
            }
        });

        container.addView(xRotationContent);
    }

    private void showYRotationContent(FrameLayout container) {
        container.removeAllViews();
        View yRotationContent = LayoutInflater.from(getContext())
                .inflate(R.layout.rotation_content_y, container, false);

        TextView valueText = yRotationContent.findViewById(R.id.yRotationValueText);
        Slider slider = yRotationContent.findViewById(R.id.yRotationSlider);
        MaterialButton resetButton = yRotationContent.findViewById(R.id.yRotationResetButton);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            valueText.setText(String.format("%.0f°", value));
            if (listener != null) {
                listener.onYRotationChanged(value);
            }
        });

        resetButton.setOnClickListener(v -> {
            slider.setValue(0);
            valueText.setText("0°");
            if (listener != null) {
                listener.onYRotationChanged(0);
            }
        });

        container.addView(yRotationContent);
    }

    private void showFlipContent(FrameLayout container) {
        container.removeAllViews();
        View flipContent = LayoutInflater.from(getContext())
                .inflate(R.layout.rotation_content_flip, container, false);

        MaterialButton flipHorizontalButton = flipContent.findViewById(R.id.flipHorizontalButton);
        MaterialButton flipVerticalButton = flipContent.findViewById(R.id.flipVerticalButton);

        flipHorizontalButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFlipHorizontal();
            }
        });

        flipVerticalButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFlipVertical();
            }
        });

        container.addView(flipContent);
    }

    // ============================================================================
    // OPACITY MODE
    // ============================================================================

    private void showOpacityContent() {
        if (opacityContent == null) {
            opacityContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_opacity, contentContainer, false);
            setupOpacitySlider();
        }
        contentContainer.addView(opacityContent);
    }

    private void setupOpacitySlider() {
        TextView valueText = opacityContent.findViewById(R.id.opacityValue);
        Slider slider = opacityContent.findViewById(R.id.opacitySlider);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int opacityValue = (int) value;
            valueText.setText(opacityValue + "%");
            if (listener != null) {
                listener.onOpacityChanged(opacityValue);
            }
        });
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private List<ColorItem> createColorList() {
        List<ColorItem> colors = new ArrayList<>();

        // Material colors
        colors.add(new ColorItem(Color.parseColor("#795548"))); // Brown
        colors.add(new ColorItem(Color.parseColor("#FF5722"))); // Deep Orange
        colors.add(new ColorItem(Color.parseColor("#9C27B0"))); // Purple
        colors.add(new ColorItem(Color.parseColor("#3F51B5"))); // Indigo
        colors.add(new ColorItem(Color.parseColor("#00BCD4"))); // Cyan
        colors.add(new ColorItem(Color.parseColor("#4CAF50"))); // Green
        colors.add(new ColorItem(Color.parseColor("#FFEB3B"))); // Yellow
        colors.add(new ColorItem(Color.parseColor("#FF9800"))); // Orange

        // Basic colors
        colors.add(new ColorItem(Color.BLACK));
        colors.add(new ColorItem(Color.WHITE));
        colors.add(new ColorItem(Color.RED));
        colors.add(new ColorItem(Color.GREEN));
        colors.add(new ColorItem(Color.BLUE));
        colors.add(new ColorItem(Color.YELLOW));
        colors.add(new ColorItem(Color.CYAN));
        colors.add(new ColorItem(Color.MAGENTA));

        return colors;
    }

    private List<GradientItem> createGradientList() {
        List<GradientItem> gradients = new ArrayList<>();

        gradients.add(new GradientItem(
                new int[]{Color.parseColor("#667eea"), Color.parseColor("#764ba2")},
                "Purple Dream"));

        gradients.add(new GradientItem(
                new int[]{Color.parseColor("#f093fb"), Color.parseColor("#f5576c")},
                "Pink Sunset"));

        gradients.add(new GradientItem(
                new int[]{Color.parseColor("#4facfe"), Color.parseColor("#00f2fe")},
                "Ocean Blue"));

        gradients.add(new GradientItem(
                new int[]{Color.parseColor("#43e97b"), Color.parseColor("#38f9d7")},
                "Fresh Mint"));

        gradients.add(new GradientItem(
                new int[]{Color.parseColor("#fa709a"), Color.parseColor("#fee140")},
                "Warm Flame"));

        gradients.add(new GradientItem(
                new int[]{Color.parseColor("#30cfd0"), Color.parseColor("#330867")},
                "Deep Sea"));

        return gradients;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }
}