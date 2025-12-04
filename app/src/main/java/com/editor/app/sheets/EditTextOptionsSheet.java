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
import com.editor.app.api.models.Media;
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

public class EditTextOptionsSheet extends BottomSheetDialogFragment {
    public static final String TAG = "EditTextOptionsSheet";
    private static final String ARG_SELECTED_MODE = "selected_mode";

    // Listener interface
    public interface TextEditListener {
        // Edit mode (Canvas positioning)
        void onEditModeChanged(boolean isEditMode);

        void onCanvasPositionChanged(String direction); // LEFT, TOP, DOWN, RIGHT

        // Color mode
        void onTextColorSelected(int color);

        void onTextGradientSelected(GradientItem gradient);

        void onTextPatternSelected(Media pattern);

        void onRemoveTextColorClicked();

        // Size mode
        void onTextSizeChanged(int size);

        // Fonts mode
        void onFontSelected(String fontName);

        void onSeeAllFontsClicked();

        // Shadow mode
        void onTextShadowAngleChanged(String direction);

        void onTextShadowBlurChanged(int blur);

        void onTextShadowColorSelected(int color);

        void onTextShadowOpacityChanged(int opacity);

        void onTextShadowOff();

        // Position mode
        void onTextPositionChanged(TextPosition position);

        // Style mode
        void onTextAlignmentChanged(TextAlignment alignment);

        void onTextCaseChanged(TextCase textCase);

        void onBoldToggled(boolean isBold);

        void onItalicToggled(boolean isItalic);

        void onUnderlineToggled(boolean isUnderline);

        // Rotation mode
        void onTextRotationChanged(float degrees);

        // Curve mode
        void onTextCurveChanged(int curveAmount);

        // Auto paragraph mode
        void onAutoParagraphChanged(int paragraphWidth);

        // Line space mode
        void onLineSpaceChanged(float lineSpacing);

        // Letter space mode
        void onLetterSpaceChanged(float letterSpacing);

        // Opacity mode
        void onTextOpacityChanged(int opacity);
    }

    // Enums
    public enum TextEditMode {
        EDIT, COLOR, SIZE, FONTS, SHADOW, POSITION, STYLE,
        ROTATION, CURVE, AUTO_PARAGRAPH, LINE_SPACE, LETTER_SPACE, OPACITY
    }

    public enum TextPosition {
        VERTICAL_TOP, VERTICAL_BOTTOM, VERTICAL_CENTER,
        HORIZONTAL_LEFT, HORIZONTAL_CENTER, HORIZONTAL_RIGHT
    }

    public enum TextAlignment {
        LEFT, CENTER, RIGHT, JUSTIFY
    }

    public enum TextCase {
        NORMAL, UPPERCASE, LOWERCASE, CAPITALIZE
    }

    private TextEditListener listener;
    private ChipGroup chipGroup;
    private FrameLayout contentContainer;
    private ImageButton backButton;
    private TextEditMode currentMode = TextEditMode.EDIT;

    // Content views (cached)
    private FrameLayout shadowContentContainer;
    private View editContent, colorContent, sizeContent, fontsContent, shadowContent;
    private View positionContent, styleContent, rotationContent, curveContent;
    private View autoParagraphContent, lineSpaceContent, letterSpaceContent, opacityContent;
    private View colorSolidContent, colorGradientContent, colorPatternContent;
    private View shadowColorContent, shadowBlurContent, shadowOpacityContent;

    // Data
    private int currentTextColor = Color.BLACK;
    private GradientItem currentGradient;

    public static EditTextOptionsSheet newInstance(TextEditMode selectedMode) {
        EditTextOptionsSheet sheet = new EditTextOptionsSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_MODE, selectedMode.name());
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(TextEditListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_text_options_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipGroup = view.findViewById(R.id.textEditChipGroup);
        contentContainer = view.findViewById(R.id.textEditContentContainer);
        backButton = view.findViewById(R.id.backButton);

        // Get selected mode from arguments
        if (getArguments() != null) {
            String modeName = getArguments().getString(ARG_SELECTED_MODE);
            if (modeName != null) {
                currentMode = TextEditMode.valueOf(modeName);
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
        String[] chipTitles = {"Edit", "Color", "Size", "Fonts", "Shadow", "Position", "Style",
                "Rotation", "Curve", "AutoParagraph", "Line Space", "Letter Space", "Opacity"};
        TextEditMode[] modes = {
                TextEditMode.EDIT,
                TextEditMode.COLOR,
                TextEditMode.SIZE,
                TextEditMode.FONTS,
                TextEditMode.SHADOW,
                TextEditMode.POSITION,
                TextEditMode.STYLE,
                TextEditMode.ROTATION,
                TextEditMode.CURVE,
                TextEditMode.AUTO_PARAGRAPH,
                TextEditMode.LINE_SPACE,
                TextEditMode.LETTER_SPACE,
                TextEditMode.OPACITY
        };

        for (int i = 0; i < chipTitles.length; i++) {
            Chip chip = (Chip) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filter_chip, chipGroup, false);

            chip.setText(chipTitles[i]);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            final TextEditMode mode = modes[i];

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

    private void onChipSelected(TextEditMode mode) {
        currentMode = mode;
        contentContainer.removeAllViews();

        switch (mode) {
            case EDIT:
                showEditContent();
                break;
            case COLOR:
                showColorContent();
                break;
            case SIZE:
                showSizeContent();
                break;
            case FONTS:
                showFontsContent();
                break;
            case SHADOW:
                showShadowContent();
                break;
            case POSITION:
                showPositionContent();
                break;
            case STYLE:
                showStyleContent();
                break;
            case ROTATION:
                showRotationContent();
                break;
            case CURVE:
                showCurveContent();
                break;
            case AUTO_PARAGRAPH:
                showAutoParagraphContent();
                break;
            case LINE_SPACE:
                showLineSpaceContent();
                break;
            case LETTER_SPACE:
                showLetterSpaceContent();
                break;
            case OPACITY:
                showOpacityContent();
                break;
        }
    }

    // ============================================================================
    // EDIT MODE - Canvas Positioning
    // ============================================================================

    private void showEditContent() {
        if (editContent == null) {
            editContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_edit_content, contentContainer, false);
            setupEditButtons();
        }
        contentContainer.addView(editContent);
    }

    private void setupEditButtons() {
        MaterialButton editButton = editContent.findViewById(R.id.editModeButton);
        MaterialButton leftButton = editContent.findViewById(R.id.moveLeftButton);
        MaterialButton topButton = editContent.findViewById(R.id.moveTopButton);
        MaterialButton downButton = editContent.findViewById(R.id.moveDownButton);
        MaterialButton rightButton = editContent.findViewById(R.id.moveRightButton);

        editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditModeChanged(true);
            }
        });

        leftButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCanvasPositionChanged("LEFT");
            }
        });

        topButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCanvasPositionChanged("TOP");
            }
        });

        downButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCanvasPositionChanged("DOWN");
            }
        });

        rightButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCanvasPositionChanged("RIGHT");
            }
        });
    }

    // ============================================================================
    // COLOR MODE
    // ============================================================================

    private void showColorContent() {
        if (colorContent == null) {
            colorContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_color_content, contentContainer, false);
            setupColorTabs();
        }
        contentContainer.addView(colorContent);
    }

    private void setupColorTabs() {
        FrameLayout tabContentContainer = colorContent.findViewById(R.id.textColorTabContentContainer);
        ChipGroup chipGroup = colorContent.findViewById(R.id.textColorChipGroup);

        // Default: show solid
        showSolidColorContent(tabContentContainer);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int selectedId = checkedIds.get(0);

            if (selectedId == R.id.chipSolid) {
                showSolidColorContent(tabContentContainer);
            } else if (selectedId == R.id.chipGradient) {
                showGradientColorContent(tabContentContainer);
            } else if (selectedId == R.id.chipPattern) {
                showPatternColorContent(tabContentContainer);
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
            currentTextColor = color;
            if (listener != null) {
                listener.onTextColorSelected(color);
            }
        });

        removeColorCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveTextColorClicked();
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
            setupGradientPickers();
        }
        container.addView(colorGradientContent);
    }

    private void setupGradientPickers() {
        MaterialCardView gradientPickerCard = colorGradientContent.findViewById(R.id.gradientPickerCard);
        MaterialCardView removeGradientCard = colorGradientContent.findViewById(R.id.removeColorCard);

        removeGradientCard.setVisibility(View.VISIBLE);

        List<GradientItem> gradients = createGradientList();
        LinearLayout gradientsContainer = colorGradientContent.findViewById(R.id.gradientsContainer);

        new GradientViewBinder(gradientsContainer, gradients, (gradient, position) -> {
            currentGradient = gradient;
            if (listener != null) {
                listener.onTextGradientSelected(gradient);
            }
        });

        gradientPickerCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Gradient picker coming soon!", Toast.LENGTH_SHORT).show();
        });

        removeGradientCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveTextColorClicked();
            }
        });
    }

    private void showPatternColorContent(FrameLayout container) {
        container.removeAllViews();
        if (colorPatternContent == null) {
            colorPatternContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_pattern, container, false);
            setupPatternPickers();
        }
        container.addView(colorPatternContent);
    }

    private void setupPatternPickers() {
//        MaterialButton seeAllButton = colorPatternContent.findViewById(R.id.seeAllButton);
//
//        seeAllButton.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Pattern gallery coming soon!", Toast.LENGTH_SHORT).show();
//        });

        // TODO: Wire up pattern selection
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
        TextView valueText = sizeContent.findViewById(R.id.sizeValue);
        Slider slider = sizeContent.findViewById(R.id.sizeSlider);

        // Configure for text size (8-200)
        slider.setValueFrom(8);
        slider.setValueTo(200);
        slider.setValue(16);

        valueText.setText("16sp");

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int sizeValue = (int) value;
            valueText.setText(sizeValue + "sp");
            if (listener != null) {
                listener.onTextSizeChanged(sizeValue);
            }
        });
    }

    // ============================================================================
    // FONTS MODE
    // ============================================================================

    private void showFontsContent() {
//        if (fontsContent == null) {
//            fontsContent = LayoutInflater.from(getContext())
//                    .inflate(R.layout.text_fonts_content, contentContainer, false);
//            setupFontButtons();
//        }
//        contentContainer.addView(fontsContent);
    }

    private void setupFontButtons() {
//        MaterialButton seeAllFontsButton = fontsContent.findViewById(R.id.seeAllFontsButton);
//
//        seeAllFontsButton.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onSeeAllFontsClicked();
//            }
//        });

        // TODO: Setup font preview grid
        // Add font cards in horizontal RecyclerView
    }

    // ============================================================================
    // SHADOW MODE (Reused from ImageEditBottomSheet)
    // ============================================================================

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
                    listener.onTextShadowOff();
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
            if (listener != null) listener.onTextShadowAngleChanged("LEFT");
        });

        upButton.setOnClickListener(v -> {
            if (listener != null) listener.onTextShadowAngleChanged("UP");
        });

        downButton.setOnClickListener(v -> {
            if (listener != null) listener.onTextShadowAngleChanged("DOWN");
        });

        rightButton.setOnClickListener(v -> {
            if (listener != null) listener.onTextShadowAngleChanged("RIGHT");
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

        slider.setValueFrom(0);
        slider.setValueTo(50);
        slider.setValue(0);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int blurValue = (int) value;
            valueText.setText(blurValue + "px");
            if (listener != null) {
                listener.onTextShadowBlurChanged(blurValue);
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
        MaterialCardView removeCard = shadowColorContent.findViewById(R.id.removeColorCard);
        removeCard.setVisibility(View.GONE);

        List<ColorItem> colors = createColorList();
        LinearLayout colorsContainer = shadowColorContent.findViewById(R.id.colorsContainer);

        new ColorViewBinder(colorsContainer, colors, (color, position) -> {
            if (listener != null) {
                listener.onTextShadowColorSelected(color);
            }
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
                listener.onTextShadowOpacityChanged(opacityValue);
            }
        });
    }

    // ============================================================================
    // POSITION MODE (Reused from ImageEditBottomSheet)
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
            if (listener != null) listener.onTextPositionChanged(TextPosition.VERTICAL_TOP);
        });

        verticalCenterBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextPositionChanged(TextPosition.VERTICAL_CENTER);
        });

        verticalBottomBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextPositionChanged(TextPosition.VERTICAL_BOTTOM);
        });

        horizCenterBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextPositionChanged(TextPosition.HORIZONTAL_CENTER);
        });

        horizRightBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextPositionChanged(TextPosition.HORIZONTAL_RIGHT);
        });

        horizLeftBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextPositionChanged(TextPosition.HORIZONTAL_LEFT);
        });
    }

    // ============================================================================
    // STYLE MODE
    // ============================================================================

    private void showStyleContent() {
        if (styleContent == null) {
            styleContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_style_content, contentContainer, false);
            setupStyleButtons();
        }
        contentContainer.addView(styleContent);
    }

    private void setupStyleButtons() {
        // Alignment buttons
        ImageButton alignLeftBtn = styleContent.findViewById(R.id.alignLeftButton);
        ImageButton alignCenterBtn = styleContent.findViewById(R.id.alignCenterButton);
        ImageButton alignRightBtn = styleContent.findViewById(R.id.alignRightButton);
        ImageButton alignJustifyBtn = styleContent.findViewById(R.id.alignJustifyButton);

        // Text case buttons
        ImageButton uppercaseBtn = styleContent.findViewById(R.id.uppercaseButton);
        ImageButton lowercaseBtn = styleContent.findViewById(R.id.lowercaseButton);
        ImageButton capitalizeBtn = styleContent.findViewById(R.id.capitalizeButton);

        // Text style buttons
        ImageButton boldBtn = styleContent.findViewById(R.id.boldButton);
        ImageButton italicBtn = styleContent.findViewById(R.id.italicButton);
        ImageButton underlineBtn = styleContent.findViewById(R.id.underlineButton);

        alignLeftBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextAlignmentChanged(TextAlignment.LEFT);
        });

        alignCenterBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextAlignmentChanged(TextAlignment.CENTER);
        });

        alignRightBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextAlignmentChanged(TextAlignment.RIGHT);
        });

        alignJustifyBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextAlignmentChanged(TextAlignment.JUSTIFY);
        });

        uppercaseBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextCaseChanged(TextCase.UPPERCASE);
        });

        lowercaseBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextCaseChanged(TextCase.LOWERCASE);
        });

        capitalizeBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTextCaseChanged(TextCase.CAPITALIZE);
        });

        boldBtn.setOnClickListener(v -> {
            boldBtn.setSelected(!boldBtn.isSelected());
            if (listener != null) listener.onBoldToggled(boldBtn.isSelected());
        });

        italicBtn.setOnClickListener(v -> {
            italicBtn.setSelected(!italicBtn.isSelected());
            if (listener != null) listener.onItalicToggled(italicBtn.isSelected());
        });

        underlineBtn.setOnClickListener(v -> {
            underlineBtn.setSelected(!underlineBtn.isSelected());
            if (listener != null) listener.onUnderlineToggled(underlineBtn.isSelected());
        });
    }

    // ============================================================================
    // ROTATION MODE
    // ============================================================================

    private void showRotationContent() {
        if (rotationContent == null) {
            rotationContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_rotation_content, contentContainer, false);
            setupRotationSlider();
        }
        contentContainer.addView(rotationContent);
    }

    private void setupRotationSlider() {
        TextView valueText = rotationContent.findViewById(R.id.rotationValueText);
        Slider slider = rotationContent.findViewById(R.id.rotationSlider);
        MaterialButton resetButton = rotationContent.findViewById(R.id.rotationResetButton);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            valueText.setText(String.format("%.0f°", value));
            if (listener != null) {
                listener.onTextRotationChanged(value);
            }
        });

        resetButton.setOnClickListener(v -> {
            slider.setValue(0);
            valueText.setText("0°");
            if (listener != null) {
                listener.onTextRotationChanged(0);
            }
        });
    }

    // ============================================================================
    // CURVE MODE
    // ============================================================================

    private void showCurveContent() {
        if (curveContent == null) {
            curveContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_curve_content, contentContainer, false);
            setupCurveSlider();
        }
        contentContainer.addView(curveContent);
    }

    private void setupCurveSlider() {
        TextView valueText = curveContent.findViewById(R.id.curveValueText);
        Slider slider = curveContent.findViewById(R.id.curveSlider);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int curveValue = (int) value;
            valueText.setText(String.valueOf(curveValue));
            if (listener != null) {
                listener.onTextCurveChanged(curveValue);
            }
        });
    }

    // ============================================================================
    // AUTO PARAGRAPH MODE
    // ============================================================================

    private void showAutoParagraphContent() {
        if (autoParagraphContent == null) {
            autoParagraphContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_auto_paragraph_content, contentContainer, false);
            setupAutoParagraphSlider();
        }
        contentContainer.addView(autoParagraphContent);
    }

    private void setupAutoParagraphSlider() {
        TextView valueText = autoParagraphContent.findViewById(R.id.autoParagraphValueText);
        Slider slider = autoParagraphContent.findViewById(R.id.autoParagraphSlider);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            int widthValue = (int) value;
            valueText.setText(widthValue + "px");
            if (listener != null) {
                listener.onAutoParagraphChanged(widthValue);
            }
        });
    }

    // ============================================================================
    // LINE SPACE MODE
    // ============================================================================

    private void showLineSpaceContent() {
        if (lineSpaceContent == null) {
            lineSpaceContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_line_space_content, contentContainer, false);
            setupLineSpaceSlider();
        }
        contentContainer.addView(lineSpaceContent);
    }

    private void setupLineSpaceSlider() {
        TextView valueText = lineSpaceContent.findViewById(R.id.lineSpaceValueText);
        Slider slider = lineSpaceContent.findViewById(R.id.lineSpaceSlider);

        // Default line spacing is 1.0
        slider.setValue(1.0f);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            valueText.setText(String.format("%.1f", value));
            if (listener != null) {
                listener.onLineSpaceChanged(value);
            }
        });
    }

    // ============================================================================
    // LETTER SPACE MODE
    // ============================================================================

    private void showLetterSpaceContent() {
        if (letterSpaceContent == null) {
            letterSpaceContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.text_letter_space_content, contentContainer, false);
            setupLetterSpaceSlider();
        }
        contentContainer.addView(letterSpaceContent);
    }

    private void setupLetterSpaceSlider() {
        TextView valueText = letterSpaceContent.findViewById(R.id.letterSpaceValueText);
        Slider slider = letterSpaceContent.findViewById(R.id.letterSpaceSlider);

        slider.addOnChangeListener((sliderView, value, fromUser) -> {
            valueText.setText(String.format("%.2f", value));
            if (listener != null) {
                listener.onLetterSpaceChanged(value);
            }
        });
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
                listener.onTextOpacityChanged(opacityValue);
            }
        });
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private List<ColorItem> createColorList() {
        List<ColorItem> colors = new ArrayList<>();

        colors.add(new ColorItem(Color.parseColor("#795548")));
        colors.add(new ColorItem(Color.parseColor("#FF5722")));
        colors.add(new ColorItem(Color.parseColor("#9C27B0")));
        colors.add(new ColorItem(Color.parseColor("#3F51B5")));
        colors.add(new ColorItem(Color.parseColor("#00BCD4")));
        colors.add(new ColorItem(Color.parseColor("#4CAF50")));
        colors.add(new ColorItem(Color.parseColor("#FFEB3B")));
        colors.add(new ColorItem(Color.parseColor("#FF9800")));

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