package com.epicodus.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

/**
 * Created by Guest on 5/12/16.
 */
public class Blackhole {
    float height;
    float width;
    float x;
    float y;
    RectF rect;
    Bitmap bitmap;

    public Blackhole(Context context, float screenX, float screenY, float x, float y) {
        this.x = x;
        this.y = y;
        this.width = screenX/10;
        this.height = screenY/5;
        rect = new RectF(x, y, x+width, y+height);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blackhole);
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width), (int)(height), false);
    }

    public RectF getRect() {
        return rect;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

}
