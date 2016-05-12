package com.epicodus.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

/**
 * Created by Guest on 5/12/16.
 */
public class Building {
    private Bitmap bitmap;
    private float x;
    private float y;
    private RectF rect;
    private boolean isVisible;



    boolean isExploding;

    public Building(Context context, float screenX, float screenY, float x, float y) {
        float width = screenX/8;
        float height = screenY/4;
        this.x = x;
        this.y = y;

        isExploding = false;

        rect = new RectF(x, y, x+width, y+height);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.building);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width), (int)(height), false);

        isVisible = true;
    }

    public RectF getRect() {
        return rect;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public void setInvisible() {
        isVisible = false;
    }

    public boolean getVisibility() {
        return isVisible;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isExploding() {
        return isExploding;
    }

    public void setExploding(boolean exploding) {
        isExploding = exploding;
    }
}
