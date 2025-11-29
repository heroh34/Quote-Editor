package com.editor.app.sheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.api.WallpaperClient;
import com.editor.app.api.models.Media;
import com.editor.app.api.models.SearchResponse;
import com.editor.app.sheets.adapters.ColorAdapter;
import com.editor.app.sheets.adapters.GradientAdapter;
import com.editor.app.sheets.adapters.TextureAdapter;
import com.editor.app.sheets.models.ColorItem;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ColorPickerBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "ColorPickerBottomSheet";
    private static final String ARG_SELECTED_MODE = "selected_mode";

    // Listener interface
    public interface ColorPickerListener {
        void onColorSelected(int color);
        void onGradientSelected(GradientItem gradient);
        void onTextureSelected(Media texture);
        void onGalleryClicked();
        void onCameraClicked();
        void onColorPickerClicked();
        void onGradientPickerClicked();
    }

    // Enums
    public enum ColorPickerMode {
        COLOR, GRADIENT, PATTERN, CHOOSE
    }

    private ColorPickerListener listener;
    private ChipGroup chipGroup;
    private FrameLayout contentContainer;
    private ImageButton backButton;
    private ColorPickerMode currentMode = ColorPickerMode.COLOR;

    // Content views (cached)
    private View colorContent, gradientContent, patternContent, chooseContent;

    // Texture data
    private TextureAdapter textureAdapter;
    private List<Media> textureList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoadingTextures = false;

    public static ColorPickerBottomSheet newInstance(ColorPickerMode selectedMode) {
        ColorPickerBottomSheet sheet = new ColorPickerBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_MODE, selectedMode.name());
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(ColorPickerListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.color_picker_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipGroup = view.findViewById(R.id.colorPickerChipGroup);
        contentContainer = view.findViewById(R.id.colorPickerContentContainer);
        backButton = view.findViewById(R.id.backButton);

        // Get selected mode from arguments
        if (getArguments() != null) {
            String modeName = getArguments().getString(ARG_SELECTED_MODE);
            if (modeName != null) {
                currentMode = ColorPickerMode.valueOf(modeName);
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
        String[] chipTitles = {"Color", "Gradient", "Pattern", "Choose"};
        ColorPickerMode[] modes = {
                ColorPickerMode.COLOR,
                ColorPickerMode.GRADIENT,
                ColorPickerMode.PATTERN,
                ColorPickerMode.CHOOSE
        };

        for (int i = 0; i < chipTitles.length; i++) {
            Chip chip = (Chip) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_filter_chip, chipGroup, false);

            chip.setText(chipTitles[i]);
            chip.setCheckable(true);
            chip.setId(View.generateViewId());

            final ColorPickerMode mode = modes[i];

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

    private void onChipSelected(ColorPickerMode mode) {
        currentMode = mode;
        contentContainer.removeAllViews();

        switch (mode) {
            case COLOR:
                showColorContent();
                break;
            case GRADIENT:
                showGradientContent();
                break;
            case PATTERN:
                showPatternContent();
                break;
            case CHOOSE:
                showChooseContent();
                break;
        }
    }

    private void showColorContent() {
        if (colorContent == null) {
            colorContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_solid, contentContainer, false);

            setupColorPickers();
        }
        contentContainer.addView(colorContent);
    }

    private void setupColorPickers() {
        MaterialCardView gradientPickerCard = colorContent.findViewById(R.id.gradientPickerCard);
        MaterialCardView eyedropperCard = colorContent.findViewById(R.id.eyedropperCard);
        RecyclerView colorsRecyclerView = colorContent.findViewById(R.id.colorsRecyclerView);

        // Setup RecyclerView
        colorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Create color list
        List<ColorItem> colors = createColorList();
        ColorAdapter adapter = new ColorAdapter(colors, (color, position) -> {
            if (listener != null) {
                listener.onColorSelected(color);
            }
        });
        colorsRecyclerView.setAdapter(adapter);

        // Picker card listener
        gradientPickerCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onColorPickerClicked();
            }
        });

        // Eyedropper card listener
        eyedropperCard.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Eyedropper coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showGradientContent() {
        if (gradientContent == null) {
            gradientContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_gradient, contentContainer, false);

            setupGradientPickers();
        }
        contentContainer.addView(gradientContent);
    }

    private void setupGradientPickers() {
        MaterialCardView gradientPickerCard = gradientContent.findViewById(R.id.gradientPickerCard);
        RecyclerView gradientsRecyclerView = gradientContent.findViewById(R.id.gradientsRecyclerView);

        // Setup RecyclerView
        gradientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Create gradient list
        List<GradientItem> gradients = createGradientList();
        GradientAdapter adapter = new GradientAdapter(gradients, (gradient, position) -> {
            if (listener != null) {
                listener.onGradientSelected(gradient);
            }
        });
        gradientsRecyclerView.setAdapter(adapter);

        // Picker card listener
        gradientPickerCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGradientPickerClicked();
            }
        });
    }

    private void showStockImageContent() {
        // Reuse pattern content for stock images
        showPatternContent();
    }

    private void showPatternContent() {
        if (patternContent == null) {
            patternContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_pattern, contentContainer, false);

            setupPatternPickers();
        }
        contentContainer.addView(patternContent);
    }

    private void setupPatternPickers() {
        MaterialCardView seeAllCard = patternContent.findViewById(R.id.seeAllPatternsCard);
        RecyclerView texturesRecyclerView = patternContent.findViewById(R.id.patternsRecyclerView);

        // Setup RecyclerView with horizontal layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        texturesRecyclerView.setLayoutManager(layoutManager);

        // Setup adapter
        textureAdapter = new TextureAdapter(textureList, (texture, position) -> {
            if (listener != null) {
                listener.onTextureSelected(texture);
            }
        });
        texturesRecyclerView.setAdapter(textureAdapter);

        // Load textures from Unsplash
        loadTexturesFromUnsplash();

        // Pagination on scroll
        texturesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager != null) {
                    int visibleItemCount = manager.getChildCount();
                    int totalItemCount = manager.getItemCount();
                    int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();

                    if (!isLoadingTextures && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                        loadTexturesFromUnsplash();
                    }
                }
            }
        });

        // See All button - hide for now since we have pagination
        seeAllCard.setVisibility(View.GONE);
    }

    private void loadTexturesFromUnsplash() {
        if (isLoadingTextures) return;

        isLoadingTextures = true;

        // Use different search query based on mode
        String searchQuery = "texture pattern";

        WallpaperClient.getApi().getSearchMedia(
                "photos",
                currentPage,
                20,
                "landscape",
                searchQuery
        ).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                isLoadingTextures = false;

                if (response.isSuccessful() && response.body() != null) {
                    SearchResponse searchResponse = response.body();
                    List<Media> newTextures = searchResponse.getResults();

                    if (newTextures != null && !newTextures.isEmpty()) {
                        if (textureAdapter != null) {
                            textureAdapter.addTextures(newTextures);
                            currentPage++;
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                isLoadingTextures = false;
                Toast.makeText(getContext(), "Failed to load images", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChooseContent() {
        if (chooseContent == null) {
            chooseContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.edit_content_choose, contentContainer, false);

            setupChooseButtons();
        }
        contentContainer.addView(chooseContent);
    }

    private void setupChooseButtons() {
        MaterialButton galleryButton = chooseContent.findViewById(R.id.galleryButton);
        MaterialButton cameraButton = chooseContent.findViewById(R.id.cameraButton);

        galleryButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGalleryClicked();
            }
            dismiss();
        });

        cameraButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCameraClicked();
            }
            dismiss();
        });
    }

    private List<ColorItem> createColorList() {
        List<ColorItem> colors = new ArrayList<>();

        // Material colors
        colors.add(new ColorItem(android.graphics.Color.parseColor("#795548"))); // Brown
        colors.add(new ColorItem(android.graphics.Color.parseColor("#FF5722"))); // Deep Orange
        colors.add(new ColorItem(android.graphics.Color.parseColor("#9C27B0"))); // Purple
        colors.add(new ColorItem(android.graphics.Color.parseColor("#3F51B5"))); // Indigo
        colors.add(new ColorItem(android.graphics.Color.parseColor("#00BCD4"))); // Cyan
        colors.add(new ColorItem(android.graphics.Color.parseColor("#4CAF50"))); // Green
        colors.add(new ColorItem(android.graphics.Color.parseColor("#FFEB3B"))); // Yellow
        colors.add(new ColorItem(android.graphics.Color.parseColor("#FF9800"))); // Orange

        // Basic colors
        colors.add(new ColorItem(android.graphics.Color.BLACK));
        colors.add(new ColorItem(android.graphics.Color.WHITE));
        colors.add(new ColorItem(android.graphics.Color.RED));
        colors.add(new ColorItem(android.graphics.Color.GREEN));
        colors.add(new ColorItem(android.graphics.Color.BLUE));
        colors.add(new ColorItem(android.graphics.Color.YELLOW));
        colors.add(new ColorItem(android.graphics.Color.CYAN));
        colors.add(new ColorItem(android.graphics.Color.MAGENTA));

        return colors;
    }

    private List<GradientItem> createGradientList() {
        List<GradientItem> gradients = new ArrayList<>();

        gradients.add(new GradientItem(
                new int[]{android.graphics.Color.parseColor("#667eea"), android.graphics.Color.parseColor("#764ba2")},
                "Purple Dream"));

        gradients.add(new GradientItem(
                new int[]{android.graphics.Color.parseColor("#f093fb"), android.graphics.Color.parseColor("#f5576c")},
                "Pink Sunset"));

        gradients.add(new GradientItem(
                new int[]{android.graphics.Color.parseColor("#4facfe"), android.graphics.Color.parseColor("#00f2fe")},
                "Ocean Blue"));

        gradients.add(new GradientItem(
                new int[]{android.graphics.Color.parseColor("#43e97b"), android.graphics.Color.parseColor("#38f9d7")},
                "Fresh Mint"));

        gradients.add(new GradientItem(
                new int[]{android.graphics.Color.parseColor("#fa709a"), android.graphics.Color.parseColor("#fee140")},
                "Warm Flame"));

        gradients.add(new GradientItem(
                new int[]{android.graphics.Color.parseColor("#30cfd0"), android.graphics.Color.parseColor("#330867")},
                "Deep Sea"));

        return gradients;
    }
}