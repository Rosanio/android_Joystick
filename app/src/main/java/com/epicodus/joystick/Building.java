package com.epicodus.joystick;

import android.graphics.RectF;

/**
 * Created by Guest on 5/12/16.
 */
public class Building {
    private float x;
    private float y;

    private float width;
    private float height;
    private RectF rect;
    private boolean isVisible;

    public Building(float screenX, float screenY) {
        x = screenX/2;
        y = (float)(screenY*0.6);
        width = screenX/8;
        height = screenY;

        rect = new RectF(x, y, x+width, y+height);
    }

    public RectF getRect() {
        return rect;
    }

    public void setInvisible() {
        isVisible = false;
    }

    public boolean getVisibility() {
        return isVisible;
    }
}
