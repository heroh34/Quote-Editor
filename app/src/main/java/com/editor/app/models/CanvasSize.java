package com.editor.app.models;

public class CanvasSize {
    private String name;
    private String dimensions;
    private int width;
    private int height;
    private String category; // "standard", "basic", "social_media"
    private int iconResId;

    public CanvasSize(String name, String dimensions, int width, int height, String category) {
        this.name = name;
        this.dimensions = dimensions;
        this.width = width;
        this.height = height;
        this.category = category;
    }

    public CanvasSize(String name, String dimensions, int width, int height, String category, int iconResId) {
        this(name, dimensions, width, height, category);
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public String getDimensions() {
        return dimensions;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getCategory() {
        return category;
    }

    public int getIconResId() {
        return iconResId;
    }
}
