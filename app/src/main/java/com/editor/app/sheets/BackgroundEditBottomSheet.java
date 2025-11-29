package com.editor.app.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.editor.app.R;
import com.editor.app.api.models.Media;
import com.editor.app.sheets.background.BorderOptionsView;
import com.editor.app.sheets.background.EditOptionsView;
import com.editor.app.sheets.background.OpacityOptionsView;
import com.editor.app.sheets.background.ScaleOptionsView;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class BackgroundEditBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "BackgroundEditBottomSheet";

    // Listener interface
    public interface EditOptionsListener {
        void onBorderStateChanged(boolean isOn, int size, int opacity);

        void onBorderTypeChanged(BorderType type, int color);

        void onSolidColorSelected(int color);

        void onGradientSelected(GradientItem gradient);

        void onTextureSelected(Media texture);

        void onOpacityChanged(int opacity);

        void onScaleChanged(int scale);

        void onScaleTypeChanged(ScaleType scaleType);

        void onBackgroundColorSelected(int color);

        void onBackgroundGradientSelected(GradientItem gradient);

        void onBackgroundTextureSelected(Media texture);

        void onGalleryClicked();

        void onCameraClicked();
    }

    // Enums
    public enum EditMode {
        EDIT, BORDER, OPACITY, SCALE, BLUR, FILTER, BLEND
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

    // Option views
    private EditOptionsView editOptionsView;
    private BorderOptionsView borderOptionsView;
    private OpacityOptionsView opacityOptionsView;
    private ScaleOptionsView scaleOptionsView;

    public static BackgroundEditBottomSheet newInstance() {
        return new BackgroundEditBottomSheet();
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

        setupDynamicChips();
        setupDefaultContent();
    }

    private void setupDynamicChips() {
        String[] chipTitles = {"Edit", "Border", "Opacity", "Scale", "Blur", "Filter", "Blend"};
        EditMode[] modes = {EditMode.EDIT, EditMode.BORDER, EditMode.OPACITY,
                EditMode.SCALE, EditMode.BLUR, EditMode.FILTER, EditMode.BLEND};

        for (int i = 0; i < chipTitles.length; i++) {
            Chip chip = (Chip) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filter_chip, chipGroup, false);

            chip.setText(chipTitles[i]);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            final EditMode mode = modes[i];
            chip.setOnClickListener(v -> onChipSelected(mode));

            chipGroup.addView(chip);
        }

        // Select first chip by default
        if (chipGroup.getChildCount() > 0) {
            ((Chip) chipGroup.getChildAt(0)).setChecked(true);
        }
    }

    private void setupDefaultContent() {
        // Show Edit options by default
        showEditOptions();
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
                // TODO: Implement Blur options
                break;
            case FILTER:
                // TODO: Implement Filter options
                break;
            case BLEND:
                // TODO: Implement Blend options
                break;
        }
    }

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
                // TODO: Open advanced color picker
            }

            @Override
            public void onGradientPickerClicked() {
                // TODO: Open gradient editor
            }
        });

        // Show the color picker sheet
        colorPickerSheet.show(getParentFragmentManager(), ColorPickerBottomSheet.TAG);
    }

    private void showBorderOptions() {
        if (borderOptionsView == null) {
            borderOptionsView = new BorderOptionsView(requireContext());
            borderOptionsView.setListener(new BorderOptionsView.BorderOptionsViewListener() {
                @Override
                public void onBorderStateChanged(boolean isOn, int size, int opacity) {
                    if (listener != null) listener.onBorderStateChanged(isOn, size, opacity);
                }

                @Override
                public void onBorderTypeChanged(BorderType type, int color) {
                    if (listener != null) listener.onBorderTypeChanged(type, color);
                }

                @Override
                public void onSolidColorSelected(int color) {
                    if (listener != null) listener.onSolidColorSelected(color);
                }

                @Override
                public void onGradientSelected(GradientItem gradient) {
                    if (listener != null) listener.onGradientSelected(gradient);
                }

                @Override
                public void onTextureSelected(Media texture) {
                    if (listener != null) listener.onTextureSelected(texture);
                }

                @Override
                public void onColorPickerClicked() {
                    // TODO: Open advanced color picker
                }

                @Override
                public void onGradientPickerClicked() {
                    // TODO: Open gradient editor
                }

                @Override
                public void onSeeAllTexturesClicked() {
                    // TODO: Open full texture gallery
                }
            });
        }
        contentContainer.addView(borderOptionsView);
    }

    private void showOpacityOptions() {
        if (opacityOptionsView == null) {
            opacityOptionsView = new OpacityOptionsView(requireContext());
            opacityOptionsView.setListener(opacity -> {
                if (listener != null) listener.onOpacityChanged(opacity);
            });
        }
        contentContainer.addView(opacityOptionsView);
    }

    private void showScaleOptions() {
        if (scaleOptionsView == null) {
            scaleOptionsView = new ScaleOptionsView(requireContext());
            scaleOptionsView.setListener(new ScaleOptionsView.ScaleOptionsViewListener() {
                @Override
                public void onScaleChanged(int scale) {
                    if (listener != null) listener.onScaleChanged(scale);
                }

                @Override
                public void onScaleTypeChanged(ScaleType scaleType) {
                    if (listener != null) listener.onScaleTypeChanged(scaleType);
                }
            });
        }
        contentContainer.addView(scaleOptionsView);
    }
}