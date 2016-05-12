package com.epicodus.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

/**
 * Created by Guest on 5/12/16.
 */
public class Tornado {
    private Bitmap bitmap;
    private float x;
    private float y;

    private float width;
    private float height;


    private RectF rect;

    float xVel;
    float yVel;

    public Tornado(Context context, float screenX, float screenY) {
        x = (float)(screenX*1.4);
        y = screenY/20;
        width = screenX/8;
        height = (float)(screenY/6);
        xVel = 0;
        yVel = 0;
        rect = new RectF();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tornado);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width), (int)(height), false);
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public RectF getRect() {
        return rect;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void update(long fps, float circleXPosition, float circleYPosition, float screenWidth, float screenHeight) {
        xVel = (circleXPosition - (float) (screenWidth*0.85))/8;
        yVel = (circleYPosition - (float) (screenHeight*0.75))/8;
        x = x +xVel;
        y = y + yVel;

        rect.top = y;
        rect.bottom = y+height;
        rect.left = x;
        rect.right = x+width;
    }
}
