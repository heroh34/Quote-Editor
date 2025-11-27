package com.editor.app.managers;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DragTouchListener implements View.OnTouchListener {

    private float dX, dY;
    private int lastAction;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                float newX = event.getRawX() + dX;
                float newY = event.getRawY() + dY;

                // Keep element within bounds
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) {
                    newX = Math.max(0, Math.min(newX, parent.getWidth() - view.getWidth()));
                    newY = Math.max(0, Math.min(newY, parent.getHeight() - view.getHeight()));
                }

                view.setX(newX);
                view.setY(newY);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN) {
                    view.performClick();
                }
                break;

            default:
                return false;
        }

        return true;
    }
}
