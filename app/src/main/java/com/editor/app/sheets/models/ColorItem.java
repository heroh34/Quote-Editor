package com.editor.app.sheets.models;
public class ColorItem {
    private int color;
    private boolean isSelected;

    public ColorItem(int color) {
        this.color = color;
        this.isSelected = false;
    }

    public ColorItem(int color, boolean isSelected) {
        this.color = color;
        this.isSelected = isSelected;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
