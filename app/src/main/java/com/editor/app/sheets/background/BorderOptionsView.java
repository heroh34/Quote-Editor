package com.editor.app.sheets.background;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.editor.app.R;
import com.editor.app.api.WallpaperClient;
import com.editor.app.api.models.Media;
import com.editor.app.api.models.SearchResponse;
import com.editor.app.sheets.BackgroundEditBottomSheet.BorderType;
import com.editor.app.sheets.adapters.ColorAdapter;
import com.editor.app.sheets.adapters.GradientAdapter;
import com.editor.app.sheets.adapters.TextureAdapter;
import com.editor.app.sheets.models.ColorItem;
import com.editor.app.sheets.models.GradientItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BorderOptionsView extends LinearLayout {
    private static final String TAG = "BorderOptionsView";

    public interface BorderOptionsViewListener {
        void onBorderStateChanged(boolean isOn, int size, int opacity);
        void onBorderTypeChanged(BorderType type, int color);
        void onSolidColorSelected(int color);
        void onGradientSelected(GradientItem gradient);
        void onTextureSelected(Media texture);
        void onColorPickerClicked();
        void onGradientPickerClicked();
        void onSeeAllTexturesClicked();
    }

    private enum BorderSubOption {
        OFF, SIZE, SOLID, GRADIENT, PATTERN, OPACITY
    }

    private BorderOptionsViewListener listener;
    private ChipGroup borderTypeChipGroup;
    private Chip chipOff, chipSize, chipSolid, chipGradient, chipPattern, chipOpacity;
    private FrameLayout contentContainer;

    private boolean isBorderOn = false;
    private int currentSize = 5;
    private int currentOpacity = 100;
    private int currentColor = Color.BLACK;
    private BorderSubOption currentSubOption = BorderSubOption.OFF;

    // Content views (cached)
    private View offContent, sizeContent, solidContent, gradientContent, patternContent, opacityContent;

    // Texture data
    private TextureAdapter textureAdapter;
    private List<Media> textureList = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoadingTextures = false;

    public BorderOptionsView(Context context) {
        super(context);
        init(context);
    }

    public BorderOptionsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_border_options, this, true);

        setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

        setupViews();
    }

    private void setupViews() {
        borderTypeChipGroup = findViewById(R.id.borderTypeChipGroup);
        chipOff = findViewById(R.id.chipOff);
        chipSize = findViewById(R.id.chipSize);
        chipSolid = findViewById(R.id.chipSolid);
        chipGradient = findViewById(R.id.chipGradient);
        chipPattern = findViewById(R.id.chipPattern);
        chipOpacity = findViewById(R.id.chipOpacity);
        contentContainer = findViewById(R.id.borderContentContainer);

        // Set default selection
        chipOff.setChecked(true);

        // Chip listeners
        chipOff.setOnClickListener(v -> showSubOption(BorderSubOption.OFF));
        chipSize.setOnClickListener(v -> showSubOption(BorderSubOption.SIZE));
        chipSolid.setOnClickListener(v -> showSubOption(BorderSubOption.SOLID));
        chipGradient.setOnClickListener(v -> showSubOption(BorderSubOption.GRADIENT));
        chipPattern.setOnClickListener(v -> showSubOption(BorderSubOption.PATTERN));
        chipOpacity.setOnClickListener(v -> showSubOption(BorderSubOption.OPACITY));
    }

    private void showSubOption(BorderSubOption subOption) {
        currentSubOption = subOption;
        contentContainer.removeAllViews();

        // Update border state
        isBorderOn = (subOption != BorderSubOption.OFF);

        switch (subOption) {
            case OFF:
                showOffContent();
                if (listener != null) {
                    listener.onBorderTypeChanged(BorderType.OFF, 0);
                }
                break;
            case SIZE:
                showSizeContent();
                break;
            case SOLID:
                showSolidContent();
                if (listener != null) {
                    listener.onBorderTypeChanged(BorderType.SOLID, currentColor);
                }
                break;
            case GRADIENT:
                showGradientContent();
                if (listener != null) {
                    listener.onBorderTypeChanged(BorderType.GRADIENT, 0);
                }
                break;
            case PATTERN:
                showPatternContent();
                if (listener != null) {
                    listener.onBorderTypeChanged(BorderType.PATTERN, 0);
                }
                break;
            case OPACITY:
                showOpacityContent();
                break;
        }

        notifyBorderStateChanged();
    }

    private void showOffContent() {
        if (offContent == null) {
            offContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_off, contentContainer, false);

            MaterialButton enableButton = offContent.findViewById(R.id.enableBorderButton);
            enableButton.setOnClickListener(v -> {
                // Enable border and switch to Size chip
                chipSize.setChecked(true);
                showSubOption(BorderSubOption.SIZE);
            });
        }
        contentContainer.addView(offContent);
    }

    private void showSizeContent() {
        if (sizeContent == null) {
            sizeContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_size, contentContainer, false);

            SeekBar sizeSlider = sizeContent.findViewById(R.id.sizeSlider);
            TextView sizeValue = sizeContent.findViewById(R.id.sizeValue);

            sizeSlider.setProgress(currentSize);
            sizeValue.setText(currentSize + "px");

            sizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        currentSize = progress;
                        sizeValue.setText(currentSize + "px");
                        notifyBorderStateChanged();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        contentContainer.addView(sizeContent);
    }

    private void showSolidContent() {
        if (solidContent == null) {
            solidContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_solid, contentContainer, false);

            setupSolidColorPickers();
        }
        contentContainer.addView(solidContent);
    }

    private void setupSolidColorPickers() {
        MaterialCardView gradientPickerCard = solidContent.findViewById(R.id.gradientPickerCard);
        MaterialCardView eyedropperCard = solidContent.findViewById(R.id.eyedropperCard);
        RecyclerView colorsRecyclerView = solidContent.findViewById(R.id.colorsRecyclerView);

        // Setup RecyclerView
        colorsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));

        // Create color list
        List<ColorItem> colors = createColorList();
        ColorAdapter adapter = new ColorAdapter(colors, (color, position) -> {
            currentColor = color;
            if (listener != null) {
                listener.onSolidColorSelected(color);
            }
        });
        colorsRecyclerView.setAdapter(adapter);

        // Picker card listeners
        gradientPickerCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onColorPickerClicked();
            }
        });

        eyedropperCard.setOnClickListener(v -> {
            // TODO: Implement eyedropper functionality
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
        gradientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), HORIZONTAL, false);
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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

        // See All button listener
        seeAllCard.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSeeAllTexturesClicked();
            }
        });
    }

    private void loadTexturesFromUnsplash() {
        if (isLoadingTextures) return;

        isLoadingTextures = true;

        WallpaperClient.getApi().getSearchMedia(
                "photos",
                currentPage,
                20,
                "landscape",
                "texture pattern"
        ).enqueue(new Callback<>() {
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
                        Log.d(TAG, "Loaded " + newTextures.size() + " textures from page " + currentPage);
                    }
                } else {
                    Log.e(TAG, "Failed to load textures: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                isLoadingTextures = false;
                Log.e(TAG, "Error loading textures", t);
                Toast.makeText(getContext(), "Failed to load textures", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOpacityContent() {
        if (opacityContent == null) {
            opacityContent = LayoutInflater.from(getContext())
                    .inflate(R.layout.border_content_opacity, contentContainer, false);

            SeekBar opacitySlider = opacityContent.findViewById(R.id.opacitySlider);
            TextView opacityValue = opacityContent.findViewById(R.id.opacityValue);

            opacitySlider.setProgress(currentOpacity);
            opacityValue.setText(currentOpacity + "%");

            opacitySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        currentOpacity = progress;
                        opacityValue.setText(currentOpacity + "%");
                        notifyBorderStateChanged();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
        contentContainer.addView(opacityContent);
    }

    private List<ColorItem> createColorList() {
        List<ColorItem> colors = new ArrayList<>();

        // Additional colors
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

    private void notifyBorderStateChanged() {
        if (listener != null) {
            listener.onBorderStateChanged(isBorderOn, currentSize, currentOpacity);
        }
    }

    public void setListener(BorderOptionsViewListener listener) {
        this.listener = listener;
    }

    // Getters
    public boolean isBorderOn() {
        return isBorderOn;
    }

    public int getCurrentSize() {
        return currentSize;
    }

    public int getCurrentOpacity() {
        return currentOpacity;
    }

    public int getCurrentColor() {
        return currentColor;
    }
}