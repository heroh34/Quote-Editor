package com.editor.app.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.editor.app.R;
import com.editor.app.api.models.Media;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

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

        // AI Remover mode
        void onAIRemoveBackground();

        // Color mode
        void onColorSelected(int color);
        void onColorGradientSelected(GradientItem gradient);
        void onRemoveColorClicked();

        // Effects mode
        void onEffectSelected(String effectName);
        void onSeeAllEffectsClicked();

        // Hue mode
        void onHueChanged(int hue);

        // Shadow mode
        void onShadowAngleChanged(String direction); // LEFT, UP, DOWN, RIGHT
        void onShadowBlurChanged(int blur);
        void onShadowColorSelected(int color);
        void onShadowOpacityChanged(int opacity);
        void onShadowOff();
    }

    // Enums
    public enum ImageEditMode {
        EDIT, SIZE, CROP, COLOR, EFFECTS, HUE, SHADOW
    }

    private ImageEditListener listener;
    private ChipGroup chipGroup;
    private FrameLayout contentContainer;
    private ImageButton backButton;
    private ImageEditMode currentMode = ImageEditMode.EDIT;

    // Content views (cached)
    private View editContent, sizeContent, cropContent, aiRemoverContent;
    private View colorContent, effectsContent, hueContent, shadowContent;

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
        String[] chipTitles = {"Edit", "Size", "Crop", "AI Remover", "Color", "Effects", "Hue", "Shadow"};
        ImageEditMode[] modes = {
                ImageEditMode.EDIT,
                ImageEditMode.SIZE,
                ImageEditMode.CROP,
                ImageEditMode.COLOR,
                ImageEditMode.EFFECTS,
                ImageEditMode.HUE,
                ImageEditMode.SHADOW
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
            case EFFECTS:
                showEffectsContent();
                break;
            case HUE:
                showHueContent();
                break;
            case SHADOW:
                showShadowContent();
                break;
        }
    }

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

    private void showSizeContent() {
        if (sizeContent == null) {
            // TODO: Create size content with slider
            sizeContent = new View(requireContext());
        }
        contentContainer.addView(sizeContent);
    }

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

    private void showAIRemoverContent() {
        if (aiRemoverContent == null) {
            // TODO: Create AI Remover content
            aiRemoverContent = new View(requireContext());
        }
        contentContainer.addView(aiRemoverContent);
    }

    private void showColorContent() {
        if (colorContent == null) {
            colorContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.image_edit_content_color, contentContainer, false);

            setupColorTabs();
        }
        contentContainer.addView(colorContent);
    }

    private void setupColorTabs() {
        MaterialButton solidTab = colorContent.findViewById(R.id.solidTabButton);
        MaterialButton gradientTab = colorContent.findViewById(R.id.gradientTabButton);
        FrameLayout tabContentContainer = colorContent.findViewById(R.id.colorTabContentContainer);

        // Default: show solid
        solidTab.setSelected(true);
        showSolidColorContent(tabContentContainer);

        solidTab.setOnClickListener(v -> {
            solidTab.setSelected(true);
            gradientTab.setSelected(false);
            showSolidColorContent(tabContentContainer);
        });

        gradientTab.setOnClickListener(v -> {
            gradientTab.setSelected(true);
            solidTab.setSelected(false);
            showGradientColorContent(tabContentContainer);
        });
    }

    private void showSolidColorContent(FrameLayout container) {
        container.removeAllViews();
        View solidContent = LayoutInflater.from(getContext())
                .inflate(R.layout.image_color_solid_content, container, false);

        // TODO: Setup color picker, remove button, and colors RecyclerView

        container.addView(solidContent);
    }

    private void showGradientColorContent(FrameLayout container) {
        container.removeAllViews();
        View gradientContent = LayoutInflater.from(getContext())
                .inflate(R.layout.border_content_gradient, container, false);

        // TODO: Setup gradient picker and gradients RecyclerView

        container.addView(gradientContent);
    }

    private void showEffectsContent() {
        if (effectsContent == null) {
            // TODO: Create effects content with See All and effect previews
            effectsContent = new View(requireContext());
        }
        contentContainer.addView(effectsContent);
    }

    private void showHueContent() {
        if (hueContent == null) {
            // TODO: Create hue content (similar to color but for hue adjustment)
            hueContent = new View(requireContext());
        }
        contentContainer.addView(hueContent);
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
        Chip offChip = shadowContent.findViewById(R.id.shadowOffChip);
        Chip angleChip = shadowContent.findViewById(R.id.shadowAngleChip);
        Chip blurChip = shadowContent.findViewById(R.id.shadowBlurChip);
        Chip colorChip = shadowContent.findViewById(R.id.shadowColorChip);
        Chip opacityChip = shadowContent.findViewById(R.id.shadowOpacityChip);
        FrameLayout shadowContentContainer = shadowContent.findViewById(R.id.shadowContentContainer);

        // Default: Angle selected
        angleChip.setChecked(true);
        showShadowAngleContent(shadowContentContainer);

        offChip.setOnClickListener(v -> {
            shadowContentContainer.removeAllViews();
            if (listener != null) listener.onShadowOff();
        });

        angleChip.setOnClickListener(v -> showShadowAngleContent(shadowContentContainer));
        blurChip.setOnClickListener(v -> showShadowBlurContent(shadowContentContainer));
        colorChip.setOnClickListener(v -> showShadowColorContent(shadowContentContainer));
        opacityChip.setOnClickListener(v -> showShadowOpacityContent(shadowContentContainer));
    }

    private void showShadowAngleContent(FrameLayout container) {
        container.removeAllViews();
        View angleContent = LayoutInflater.from(getContext())
                .inflate(R.layout.shadow_content_angle, container, false);

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

        container.addView(angleContent);
    }

    private void showShadowBlurContent(FrameLayout container) {
        container.removeAllViews();
        // TODO: Add blur slider
    }

    private void showShadowColorContent(FrameLayout container) {
        container.removeAllViews();
        // TODO: Add color picker for shadow
    }

    private void showShadowOpacityContent(FrameLayout container) {
        container.removeAllViews();
        // TODO: Add opacity slider for shadow
    }
}