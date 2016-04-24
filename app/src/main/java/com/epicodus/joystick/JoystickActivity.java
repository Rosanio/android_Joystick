package com.epicodus.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class JoystickActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);
    }

    class GameView extends SurfaceView implements Runnable {
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
        boolean isBeingTouched = false;

        public GameView(Context context) {
            //Apparently this line asks the SurfaceView class to setup our object for us, whatever that means.
            super(context);
            //initialize ourHolder and paint
            ourHolder = getHolder();
            paint = new Paint();
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
                circleXPosition = pointerXPosition;
                circleYPosition = pointerYPosition;
            } else {
                circleXPosition = this.getWidth()/2;
                circleYPosition = this.getHeight()/2;
                pointerXPosition = this.getWidth()/2;
                pointerYPosition = this.getHeight()/2;
            }
        }

        public void draw() {
            if(ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                paint.setColor(Color.argb(255, 249, 129, 0));
                canvas.drawCircle(circleXPosition, circleYPosition, 100, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch(InterruptedException e) {
                Log.e("Error: ", "joining game thread");
            }
        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isBeingTouched = true;
                    pointerXPosition = motionEvent.getX();
                    pointerYPosition = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointerXPosition = motionEvent.getX();
                    pointerYPosition = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    isBeingTouched = false;
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}
