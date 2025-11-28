package com.editor.app.sheets.models;

public class GradientItem {
    private int[] colors; // Array of colors for gradient
    private String name;
    private boolean isSelected;

    public GradientItem(int[] colors, String name) {
        this.colors = colors;
        this.name = name;
        this.isSelected = false;
    }

    public GradientItem(int[] colors, String name, boolean isSelected) {
        this.colors = colors;
        this.name = name;
        this.isSelected = isSelected;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Helper method to get start and end colors for simple gradients
    public int getStartColor() {
        return colors.length > 0 ? colors[0] : 0xFF000000;
    }

    public int getEndColor() {
        return colors.length > 1 ? colors[colors.length - 1] : getStartColor();
    }
}
