package com.editor.app.sheets.models;

public class PatternItem {
    private int drawableRes; // Resource ID for pattern drawable
    private String name;
    private boolean isSelected;

    public PatternItem(int drawableRes, String name) {
        this.drawableRes = drawableRes;
        this.name = name;
        this.isSelected = false;
    }

    public PatternItem(int drawableRes, String name, boolean isSelected) {
        this.drawableRes = drawableRes;
        this.name = name;
        this.isSelected = isSelected;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
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
}
