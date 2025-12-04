package com.editor.app.sheets;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.editor.app.R;
import com.editor.app.api.models.Media;
import com.editor.app.sheets.adapters.ColorViewBinder;
import com.editor.app.sheets.background.BorderOptionsView;
import com.editor.app.sheets.background.EditOptionsView;
import com.editor.app.sheets.background.OpacityOptionsView;
import com.editor.app.sheets.background.ScaleOptionsView;
import com.editor.app.sheets.models.ColorItem;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class BackgroundEditBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "BackgroundEditBottomSheet";
    private static final String ARG_SELECTED_MODE = "selected_mode";

    // Listener interface
    public interface EditOptionsListener {
        // Border mode
        void onBorderStateChanged(boolean isOn, int size, int opacity);
        void onBorderTypeChanged(BorderType type, int color);
        void onSolidColorSelected(int color);
        void onGradientSelected(GradientItem gradient);
        void onTextureSelected(Media texture);

        // Opacity mode
        void onOpacityChanged(int opacity);

        // Scale mode
        void onScaleChanged(int scale);
        void onScaleTypeChanged(ScaleType scaleType);

        // Edit mode (Background selection)
        void onBackgroundColorSelected(int color);
        void onBackgroundGradientSelected(GradientItem gradient);
        void onBackgroundTextureSelected(Media texture);
        void onGalleryClicked();
        void onCameraClicked();

        // Blur mode
        void onBlurChanged(int blurRadius);

        // Blend mode
        void onBlendColorSelected(int color);
        void onRemoveBlendClicked();
    }

    // Enums
    public enum EditMode {
        EDIT, BORDER, OPACITY, SCALE, BLUR, BLEND
    }

    public enum BorderType {
        OFF, SOLID, GRADIENT, PATTERN
    }

    public enum ScaleType {
        ASPECT_FILL, CENTER, TOP, BOTTOM, LEFT, RIGHT, FIT
    }

    private EditOptionsListener listener;
    private ChipGroup chipGroup;
    private ViewGroup contentContainer;
    private EditMode currentMode = EditMode.EDIT;

    // Content views (cached)
    private EditOptionsView editOptionsView;
    private BorderOptionsView borderOptionsView;
    private OpacityOptionsView opacityOptionsView;
    private ScaleOptionsView scaleOptionsView;
    private View blurOptionsView;
    private View blendOptionsView;

    // Data
    private int currentBlendColor = Color.BLACK;

    public static BackgroundEditBottomSheet newInstance() {
        return new BackgroundEditBottomSheet();
    }

    public static BackgroundEditBottomSheet newInstance(EditMode selectedMode) {
        BackgroundEditBottomSheet sheet = new BackgroundEditBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_MODE, selectedMode.name());
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(EditOptionsListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.background_edit_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipGroup = view.findViewById(R.id.chipGroup);
        contentContainer = view.findViewById(R.id.contentContainer);

        ImageButton closeButton = view.findViewById(R.id.backButton);
        closeButton.setOnClickListener(v -> dismiss());

        // Get selected mode from arguments
        if (getArguments() != null) {
            String modeName = getArguments().getString(ARG_SELECTED_MODE);
            if (modeName != null) {
                currentMode = EditMode.valueOf(modeName);
            }
        }

        setupDynamicChips();
        showSelectedMode();
    }

    private void setupDynamicChips() {
        String[] chipTitles = {"Edit", "Border", "Opacity", "Scale", "Blur", "Blend"};
        EditMode[] modes = {
                EditMode.EDIT, EditMode.BORDER, EditMode.OPACITY,
                EditMode.SCALE, EditMode.BLUR, EditMode.BLEND
        };

        for (int i = 0; i < chipTitles.length; i++) {
            Chip chip = (Chip) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filter_chip, chipGroup, false);

            chip.setText(chipTitles[i]);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            final EditMode mode = modes[i];

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

    private void onChipSelected(EditMode mode) {
        currentMode = mode;
        contentContainer.removeAllViews();

        switch (mode) {
            case EDIT:
                showEditOptions();
                break;
            case BORDER:
                showBorderOptions();
                break;
            case OPACITY:
                showOpacityOptions();
                break;
            case SCALE:
                showScaleOptions();
                break;
            case BLUR:
                showBlurOptions();
                break;
            case BLEND:
                showBlendOptions();
                break;
        }
    }

    // ============================================================================
    // EDIT MODE
    // ============================================================================

    private void showEditOptions() {
        if (editOptionsView == null) {
            editOptionsView = new EditOptionsView(requireContext());
            editOptionsView.setListener(new EditOptionsView.EditOptionsViewListener() {
                @Override
                public void onColorClicked() {
                    openColorPickerSheet(ColorPickerBottomSheet.ColorPickerMode.COLOR);
                }

                @Override
                public void onGradientClicked() {
                    openColorPickerSheet(ColorPickerBottomSheet.ColorPickerMode.GRADIENT);
                }

                @Override
                public void onPatternClicked() {
                    openColorPickerSheet(ColorPickerBottomSheet.ColorPickerMode.PATTERN);
                }

                @Override
                public void onGalleryClicked() {
                    openColorPickerSheet(ColorPickerBottomSheet.ColorPickerMode.CHOOSE);
                }

                @Override
                public void onCameraClicked() {
                    openColorPickerSheet(ColorPickerBottomSheet.ColorPickerMode.CHOOSE);
                }
            });
        }
        contentContainer.addView(editOptionsView);
    }

    private void openColorPickerSheet(ColorPickerBottomSheet.ColorPickerMode mode) {
        ColorPickerBottomSheet colorPickerSheet = ColorPickerBottomSheet.newInstance(mode);
        colorPickerSheet.setListener(new ColorPickerBottomSheet.ColorPickerListener() {
            @Override
            public void onColorSelected(int color) {
                if (listener != null) {
                    listener.onBackgroundColorSelected(color);
                }
            }

            @Override
            public void onGradientSelected(GradientItem gradient) {
                if (listener != null) {
                    listener.onBackgroundGradientSelected(gradient);
                }
            }

            @Override
            public void onTextureSelected(Media texture) {
                if (listener != null) {
                    listener.onBackgroundTextureSelected(texture);
                }
            }

            @Override
            public void onGalleryClicked() {
                if (listener != null) {
                    listener.onGalleryClicked();
                }
            }

            @Override
            public void onCameraClicked() {
                if (listener != null) {
                    listener.onCameraClicked();
                }
            }

            @Override
            public void onColorPickerClicked() {
                Toast.makeText(getContext(), "Color picker coming soon!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGradientPickerClicked() {
                Toast.makeText(getContext(), "Gradient editor coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        colorPickerSheet.show(getParentFragmentManager(), ColorPickerBottomSheet.TAG);
    }

    // ============================================================================
    // BORDER MODE
    // ============================================================================

    private void showBorderOptions() {
        if (borderOptionsView == null) {
            borderOptionsView = new BorderOptionsView(requireContext());
            borderOptionsView.setListener(new BorderOptionsView.BorderOptionsViewListener() {
                @Override
                public void onBorderStateChanged(boolean isOn, int size, int opacity) {
                    if (listener != null) {
                        listener.onBorderStateChanged(isOn, size, opacity);
                    }
                }

                @Override
                public void onBorderTypeChanged(BorderType type, int color) {
                    if (listener != null) {
                        listener.onBorderTypeChanged(type, color);
                    }
                }

                @Override
                public void onSolidColorSelected(int color) {
                    if (listener != null) {
                        listener.onSolidColorSelected(color);
                    }
                }

                @Override
                public void onGradientSelected(GradientItem gradient) {
                    if (listener != null) {
                        listener.onGradientSelected(gradient);
                    }
                }

                @Override
                public void onTextureSelected(Media texture) {
                    if (listener != null) {
                        listener.onTextureSelected(texture);
                    }
                }

                @Override
                public void onColorPickerClicked() {
                    Toast.makeText(getContext(), "Color picker coming soon!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onGradientPickerClicked() {
                    Toast.makeText(getContext(), "Gradient editor coming soon!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSeeAllTexturesClicked() {
                    Toast.makeText(getContext(), "Full texture gallery coming soon!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        contentContainer.addView(borderOptionsView);
    }

    // ============================================================================
    // OPACITY MODE
    // ============================================================================

    private void showOpacityOptions() {
        if (opacityOptionsView == null) {
            opacityOptionsView = new OpacityOptionsView(requireContext());
            opacityOptionsView.setListener(opacity -> {
                if (listener != null) {
                    listener.onOpacityChanged(opacity);
                }
            });
        }
        contentContainer.addView(opacityOptionsView);
    }

    // ============================================================================
    // SCALE MODE
    // ============================================================================

    private void showScaleOptions() {
        if (scaleOptionsView == null) {
            scaleOptionsView = new ScaleOptionsView(requireContext());
            scaleOptionsView.setListener(new ScaleOptionsView.ScaleOptionsViewListener() {
                @Override
                public void onScaleChanged(int scale) {
                    if (listener != null) {
                        listener.onScaleChanged(scale);
                    }
                }

                @Override
                public void onScaleTypeChanged(ScaleType scaleType) {
                    if (listener != null) {
                        listener.onScaleTypeChanged(scaleType);
                    }
                }
            });
        }
        contentContainer.addView(scaleOptionsView);
    }

    // ============================================================================
    // BLUR MODE
    // ============================================================================

    private void showBlurOptions() {
        if (blurOptionsView == null) {
            blurOptionsView = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_size, contentContainer, false);
            setupBlurSlider();
        }
        contentContainer.addView(blurOptionsView);
    }

    private void setupBlurSlider() {
        TextView valueText = blurOptionsView.findViewById(R.id.sizeValue);
        Slider slider = blurOptionsView.findViewById(R.id.sizeSlider);

        // Configure for blur (0-25)
        slider.setValueFrom(0);
        slider.setValueTo(25);
        slider.setValue(0);

        valueText.setText("0px");

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int blurValue = (int) value;
            valueText.setText(blurValue + "px");
            if (listener != null) {
                listener.onBlurChanged(blurValue);
            }
        });
    }

    // ============================================================================
    // BLEND MODE
    // ============================================================================

    private void showBlendOptions() {
        if (blendOptionsView == null) {
            blendOptionsView = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_solid, contentContainer, false);
            setupBlendColorPickers();
        }
        contentContainer.addView(blendOptionsView);
    }

    private void setupBlendColorPickers() {
        MaterialCardView gradientPickerCard = blendOptionsView.findViewById(R.id.gradientPickerCard);
        MaterialCardView eyedropperCard = blendOptionsView.findViewById(R.id.eyedropperCard);
        MaterialCardView removeColorCard = blendOptionsView.findViewById(R.id.removeColorCard);

        // Show remove button for blend
        removeColorCard.setVisibility(View.VISIBLE);

        List<ColorItem> colors = createColorList();
        LinearLayout colorsContainer = blendOptionsView.findViewById(R.id.colorsContainer);

        new ColorViewBinder(colorsContainer, colors, (color, position) -> {
            currentBlendColor = color;
            if (listener != null) {
                listener.onBlendColorSelected(color);
            }
        });

        gradientPickerCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Color picker coming soon!", Toast.LENGTH_SHORT).show();
        });

        eyedropperCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Eyedropper coming soon!", Toast.LENGTH_SHORT).show();
        });

        removeColorCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveBlendClicked();
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

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }
}