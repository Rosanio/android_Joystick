package com.epicodus.joystick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Guest on 5/12/16.
 */
public class GameView extends SurfaceView implements Runnable {
    //Threads are basically infinite loops, i think.
    Thread gameThread = null;
    //SurfaceHolders are used to lock the screen. This one is used when the draw function is called to make sure the operating system cannot be drawing to the screen at the same time as the game is trying to.
    SurfaceHolder ourHolder;
    //the declaration volatile allows this boolean to be user both inside and outside the thread, which I imagine is very useful.
    volatile boolean playing;
    Canvas canvas;
    Paint paint;
    long fps;
    private long timeThisFrame;
    float circleXPosition;
    float circleYPosition;
    float pointerXPosition;
    float pointerYPosition;
    float rectXPosition = this.getWidth()/2;
    float rectYPosition = this.getHeight()/4;
    float rectWidth;
    float rectHeight;
    float rectXVel;
    float rectYVel;
    float deltaX;
    float deltaY;
    float distance;
    float theta;
    boolean isBeingTouched = false;
    float screenX;
    float screenY;
    Bitmap tornadoBitmap;

    public GameView(Context context, float x, float y) {
        //Apparently this line asks the SurfaceView class to setup our object for us, whatever that means.
        super(context);
        //initialize ourHolder and paint
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        rectXPosition = screenX/2;
        rectYPosition = screenY/4;
        rectWidth = 25;
        rectHeight = 25;
        rectXVel = 0;
        rectYVel = 0;

        tornadoBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.tornado);
    }

    @Override
    public void run() {
        while(playing) {
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
            //Here the total time it took these two functions to run is calculated
            timeThisFrame = System.currentTimeMillis()-startFrameTime;
            if(timeThisFrame > 0) {
                //This line is pretty much 1000ms/1s * 1 frame/timeThisFrame ms, thus getting us the frames per second
                fps = 1000 / timeThisFrame;
            }
        }
    }

    public void update() {
        //This could be where some code goes to detect if the screen is being touched, and if so get the current position of the users finger and update the center of the circle to that location.
        circleXPosition = this.getWidth()/2;
        circleYPosition = this.getHeight()/2;
        if(isBeingTouched) {
            deltaX = pointerXPosition-this.getWidth()/2;
            deltaY = pointerYPosition - this.getHeight()/2;
            distance = (float) Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
            theta = (float) Math.atan2(deltaY,deltaX);

            if(distance <= 100) {
                circleXPosition = pointerXPosition;
                circleYPosition = pointerYPosition;
            } else {
                circleXPosition = (float)(this.getWidth()/2 + 100*Math.cos(theta));
                circleYPosition = (float) (this.getHeight()/2 + 100*Math.sin(theta));
            }
            rectXVel = (circleXPosition - this.getWidth()/2)/8;
            rectYVel = (circleYPosition - this.getHeight()/2)/8;
            rectXPosition = rectXPosition + rectXVel;
            rectYPosition = rectYPosition + rectYVel;
        } else {
            pointerXPosition = this.getWidth()/2;
            pointerYPosition = this.getHeight()/2;
        }
    }

    public void draw() {
        //initialize some variables
        float startX = rectXPosition;
        float startY = (float) 0.75*screenY;
        float stopX;
        float stopY;
        float angle;
        //Make sure surface is valid, dont know what this means but program crashes if you don't
        if(ourHolder.getSurface().isValid()) {
            //this locks the drawing surface, and sets the canvas equal to the drawing surface
            canvas = ourHolder.lockCanvas();
            //sets background color
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            //sets paint color
            paint.setColor(Color.argb(255,153,153,153));
            //draws circle
            canvas.drawCircle(this.getWidth()/2, this.getHeight()/2, 100, paint);
            //updates paint color
            paint.setColor(Color.argb(255, 255, 255, 255));
            //draws other circle
            canvas.drawCircle((float)circleXPosition, (float)circleYPosition, 50, paint);
            //draw a tornado bitmap
            canvas.drawBitmap(tornadoBitmap, rectXPosition, rectYPosition, paint);
            //unlocks the canvas, which i think means the OS can draw to it again. It also posts what we've drawn to the actual screen, i think.
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        //This function will shut down our thread if the game activity is paused or stopped. Better way to handle this might be to only run update function if playing = true, so that the game data is not destroyed.
        playing = false;
        //try-catch statement is required because of the nature of the Thread class
        try {
            gameThread.join();
        } catch(InterruptedException e) {
            Log.e("Error: ", "joining game thread");
        }
    }

    public void resume() {
        //this function will initialize a new thread when the game is started
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //when the user touches the screen at first, isBeingTouched gets set to true so the circle position knows to track to the location to the users finger. Also, the pointerXPosition and pointerYPosition variables are set to the x and y location of the users finger, which conveniently uses the same set of coordinates as the canvas apparently
                isBeingTouched = true;
                pointerXPosition = motionEvent.getX();
                pointerYPosition = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //whenever the user moves their finger, and pointerX and pointerY position variables are updated to reflect this
                pointerXPosition = motionEvent.getX();
                pointerYPosition = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                //when the user removes his finger, isBeingTouched is set to false, and the update function defaults to setting the circle and pointer positions to the center of the screen
                isBeingTouched = false;
                break;
        }
        return true;
    }
}
