package com.editor.app;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.editor.app.managers.DragTouchListener;
import com.editor.app.sheets.BackgroundBottomSheet;
import com.editor.app.sheets.ImageBottomSheet;
import com.editor.app.sheets.ResizeBottomSheet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 1001;

    private FrameLayout canvas;
    private MaterialCardView canvasCard;
    private LinearLayout bottomNavContainer;
    private MaterialButton btnSave;
    private ImageButton btnUndo, btnRedo;
    private FloatingActionButton fabFrameToggle;

    private int currentCanvasWidth = 1080;
    private int currentCanvasHeight = 1080;
    private int currentBackgroundColor = Color.WHITE;
    private float currentBackgroundOpacity = 1.0f;
    private float currentBorderSize = 0f;
    private int currentBorderColor = Color.BLACK;

    private final Stack<CanvasState> undoStack = new Stack<>();
    private final Stack<CanvasState> redoStack = new Stack<>();

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();
        setupBottomNavigation();
        setupToolbar();
        setupImagePicker();

        // Request permissions
        requestPermissions();
    }
    private void initViews() {
        canvas = findViewById(R.id.canvas);
        canvasCard = findViewById(R.id.canvasCard);
        bottomNavContainer = findViewById(R.id.bottomNavContainer);
        btnSave = findViewById(R.id.btnSave);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        fabFrameToggle = findViewById(R.id.fabFrameToggle);
    }

    private void setupToolbar() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSave.setOnClickListener(v -> saveCanvas());

        btnUndo.setOnClickListener(v -> {
            if (!undoStack.isEmpty()) {
                redoStack.push(getCurrentState());
                restoreState(undoStack.pop());
            }
        });

        btnRedo.setOnClickListener(v -> {
            if (!redoStack.isEmpty()) {
                undoStack.push(getCurrentState());
                restoreState(redoStack.pop());
            }
        });

        fabFrameToggle.setOnClickListener(v -> {
            // Toggle frame visibility
            if (canvasCard.getStrokeWidth() > 0) {
                canvasCard.setStrokeWidth(0);
            } else {
                canvasCard.setStrokeWidth(2);
            }
        });
    }

    private void setupBottomNavigation() {
        addNavItem("Text", R.drawable.edit_24px, this::showTextDialog);
        addNavItem("Image", R.drawable.image_24px, this::showImagePicker);
        addNavItem("Background", R.drawable.wallpaper_24px, this::showBackgroundSheet);
        addNavItem("Graphics", R.drawable.graphics_24px, this::showPlaceholder);
        addNavItem("Shapes", R.drawable.shapes_24px, this::showPlaceholder);
        addNavItem("My Designs", R.drawable.design_services_24px, this::showPlaceholder);
        addNavItem("Resize", R.drawable.resize_24px, this::showResizeSheet);
    }

    @SuppressLint("MissingInflatedId")
    private void addNavItem(String label, int iconRes, View.OnClickListener listener) {
        View navItem = LayoutInflater.from(this).inflate(R.layout.item_bottom_nav, null);

        ImageView navIcon = navItem.findViewById(R.id.navIcon);
        TextView navLabel = navItem.findViewById(R.id.navLabel);

        navIcon.setImageResource(iconRes);
        navLabel.setText(label);

        navItem.setOnClickListener(listener);

        bottomNavContainer.addView(navItem);
    }

    private void showTextDialog(View v) {
        saveState();

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_text_input, null);
        TextInputEditText textInput = dialogView.findViewById(R.id.textInput);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());

        dialogView.findViewById(R.id.btnAdd).setOnClickListener(view -> {
            String text = textInput.getText().toString().trim();
            if (!text.isEmpty()) {
                addTextToCanvas(text);
                dialog.dismiss();
            } else {
                textInput.setError("Please enter some text");
            }
        });

        dialog.show();
    }

    private void addTextToCanvas(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(16, 16, 16, 16);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = 50;
        params.topMargin = 50;

        textView.setLayoutParams(params);

        // Make text draggable
        textView.setOnTouchListener(new DragTouchListener());

        canvas.addView(textView);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            addImageToCanvas(imageUri);
                        }
                    }
                }
        );
    }

    private void showImagePicker(View v) {
        saveState();

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void addImageToCanvas(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            int size = 300;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = 100;
            params.topMargin = 100;

            imageView.setLayoutParams(params);
            imageView.setOnTouchListener(new DragTouchListener());

            canvas.addView(imageView);

            // Show image edit sheet
            showImageEditSheet();

        } catch (IOException e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImageEditSheet() {
        ImageBottomSheet sheet = new ImageBottomSheet();
        sheet.setOnImageChangeListener(new ImageBottomSheet.OnImageChangeListener() {
            @Override
            public void onImageSizeChange(float size) {
                // TODO: Implement image size change
                Toast.makeText(MainActivity.this, "Image size: " + size + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImageColorChange(int color) {
                // TODO: Implement image color overlay
                Toast.makeText(MainActivity.this, "Image color changed", Toast.LENGTH_SHORT).show();
            }
        });
        sheet.show(getSupportFragmentManager(), "ImageBottomSheet");
    }

    private void showBackgroundSheet(View v) {
        saveState();

        BackgroundBottomSheet sheet = new BackgroundBottomSheet();
        sheet.setOnBackgroundChangeListener(new BackgroundBottomSheet.OnBackgroundChangeListener() {
            @Override
            public void onBackgroundColorChange(int color) {
                currentBackgroundColor = color;
                updateCanvasBackground();
            }

            @Override
            public void onBackgroundOpacityChange(float opacity) {
                currentBackgroundOpacity = opacity;
                updateCanvasBackground();
            }

            @Override
            public void onBorderSizeChange(float size) {
                currentBorderSize = size;
                updateCanvasBorder();
            }

            @Override
            public void onBorderColorChange(int color) {
                currentBorderColor = color;
                updateCanvasBorder();
            }

            @Override
            public void onBlurChange(float blur) {
                // TODO: Implement blur effect
                Toast.makeText(MainActivity.this, "Blur: " + blur, Toast.LENGTH_SHORT).show();
            }
        });
        sheet.show(getSupportFragmentManager(), "BackgroundBottomSheet");
    }

    private void showResizeSheet(View v) {
        saveState();

        ResizeBottomSheet sheet = new ResizeBottomSheet();
        sheet.setOnResizeListener(size -> {
            currentCanvasWidth = size.getWidth();
            currentCanvasHeight = size.getHeight();
            updateCanvasSize();
            Toast.makeText(MainActivity.this, "Canvas resized to " + size.getName(), Toast.LENGTH_SHORT).show();
        });
        sheet.show(getSupportFragmentManager(), "ResizeBottomSheet");
    }

    private void showPlaceholder(View v) {
        Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show();
    }

    private void updateCanvasBackground() {
        int adjustedColor = Color.argb(
                (int) (currentBackgroundOpacity * 255),
                Color.red(currentBackgroundColor),
                Color.green(currentBackgroundColor),
                Color.blue(currentBackgroundColor)
        );
        canvas.setBackgroundColor(adjustedColor);
    }

    private void updateCanvasBorder() {
        if (currentBorderSize > 0) {
            canvasCard.setStrokeWidth((int) currentBorderSize);
            canvasCard.setStrokeColor(currentBorderColor);
        } else {
            canvasCard.setStrokeWidth(0);
        }
    }

    private void updateCanvasSize() {
        ViewGroup.LayoutParams params = canvasCard.getLayoutParams();

        // Calculate scaling to fit within container
        int containerWidth = findViewById(R.id.canvasContainer).getWidth() - 32; // minus padding
        int containerHeight = findViewById(R.id.canvasContainer).getHeight() - 32;

        float scaleX = (float) containerWidth / currentCanvasWidth;
        float scaleY = (float) containerHeight / currentCanvasHeight;
        float scale = Math.min(scaleX, scaleY);

        params.width = (int) (currentCanvasWidth * scale);
        params.height = (int) (currentCanvasHeight * scale);

        canvasCard.setLayoutParams(params);
    }

    private void saveCanvas() {
        // Create bitmap from canvas
        Bitmap bitmap = Bitmap.createBitmap(
                canvas.getWidth(),
                canvas.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas bitmapCanvas = new Canvas(bitmap);
        canvas.draw(bitmapCanvas);

        // TODO: Save bitmap to gallery or share
        Toast.makeText(this, "Canvas saved!", Toast.LENGTH_SHORT).show();
    }

    private void saveState() {
        undoStack.push(getCurrentState());
        redoStack.clear();
    }

    private CanvasState getCurrentState() {
        return new CanvasState(
                currentBackgroundColor,
                currentBackgroundOpacity,
                currentBorderSize,
                currentBorderColor,
                currentCanvasWidth,
                currentCanvasHeight
        );
    }

    private void restoreState(CanvasState state) {
        currentBackgroundColor = state.backgroundColor;
        currentBackgroundOpacity = state.backgroundOpacity;
        currentBorderSize = state.borderSize;
        currentBorderColor = state.borderColor;
        currentCanvasWidth = state.canvasWidth;
        currentCanvasHeight = state.canvasHeight;

        updateCanvasBackground();
        updateCanvasBorder();
        updateCanvasSize();
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Inner class to store canvas state
    private static class CanvasState {
        int backgroundColor;
        float backgroundOpacity;
        float borderSize;
        int borderColor;
        int canvasWidth;
        int canvasHeight;

        CanvasState(int backgroundColor, float backgroundOpacity, float borderSize,
                    int borderColor, int canvasWidth, int canvasHeight) {
            this.backgroundColor = backgroundColor;
            this.backgroundOpacity = backgroundOpacity;
            this.borderSize = borderSize;
            this.borderColor = borderColor;
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
        }
    }
}