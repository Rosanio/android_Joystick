package com.epicodus.joystick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Date;

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
    Context mContext;
    Canvas canvas;
    Paint paint;
    long fps;
    private long timeThisFrame;
    float circleXPosition;
    float circleYPosition;
    float pointerXPosition;
    float pointerYPosition;
    float rectXPosition;
    float rectYPosition;
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
    Bitmap buildingBitmap;
    Bitmap explosion1;
    Bitmap explosion2;
    Bitmap explosion3;
    Bitmap explosion4;
    Bitmap explosion5;
    Bitmap explosion6;
    Bitmap explosion7;
    Bitmap explosion8;
    Bitmap explosion9;
    Bitmap explosion10;
    Bitmap explosion11;
    Bitmap explosion12;
    int explosionFrame;
    Tornado tornado;
    Building building;
    boolean isExploding;
    ArrayList<Building> buildings = new ArrayList<>();
    boolean gameOver;
    long gameOverStart;

    public GameView(Context context, float x, float y) {
        //Apparently this line asks the SurfaceView class to setup our object for us, whatever that means.
        super(context);
        //initialize ourHolder and paint
        mContext = context;
        ourHolder = getHolder();
        paint = new Paint();
        screenX = x;
        screenY = y;
        rectXPosition = (float)(screenX*0.9);
        rectYPosition = screenY/4;
        rectWidth = 25;
        rectHeight = 25;
        rectXVel = 0;
        rectYVel = 0;

        buildingBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.building);
        explosion1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion1);
        explosion2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion2);
        explosion3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion3);
        explosion4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion4);
        explosion5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion5);
        explosion6 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion6);
        explosion7 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion7);
        explosion8 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion8);
        explosion9 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion9);
        explosion10 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion10);
        explosion11 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion11);
        explosion12 = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion12);

        explosionFrame = 1;

        tornado = new Tornado(context, screenX, screenY);

        building = new Building(context, screenX, screenY, screenX/2, (float)(screenY*.15));
        buildings.add(new Building(context, screenX, screenY, (float)(screenX*.7), (float)(screenY*.15)));
        buildings.add(new Building(context, screenX, screenY, (float)(screenX/4), (float)(screenY*.15)));
        buildings.add(building);

        gameOverStart = 0;
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
            int counter = 0;
            for(int i = 0; i < buildings.size(); i++) {
                if(!buildings.get(i).getVisibility()) {
                    counter ++;
                }
            }
            if(counter == buildings.size() && gameOverStart == 0) {
                gameOver = true;
                gameOverStart = System.currentTimeMillis();
            }

            if(gameOver) {
                if(System.currentTimeMillis()-gameOverStart > 5000) {
                    Intent intent = new Intent(mContext, JoystickActivity.class);
                    mContext.startActivity(intent);
                }
            }
        }
    }

    public void update() {
        //This could be where some code goes to detect if the screen is being touched, and if so get the current position of the users finger and update the center of the circle to that location.
        circleXPosition = (float) (this.getWidth()*0.85);
        circleYPosition = (float) (this.getHeight()*0.75);
        if(isBeingTouched) {
            deltaX = pointerXPosition-circleXPosition;
            deltaY = pointerYPosition - circleYPosition;
            distance = (float) Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
            theta = (float) Math.atan2(deltaY,deltaX);

            if(distance <= 100) {
                circleXPosition = pointerXPosition;
                circleYPosition = pointerYPosition;
            } else {
                circleXPosition = (float)(circleXPosition + 100*Math.cos(theta));
                circleYPosition = (float) (circleYPosition + 100*Math.sin(theta));
            }

            tornado.update(fps, circleXPosition, circleYPosition, this.getWidth(), this.getHeight());
        } else {
            pointerXPosition = this.getWidth()/2;
            pointerYPosition = this.getHeight()/2;
        }
        for(int i = 0; i < buildings.size(); i++ ) {
            if(RectF.intersects(tornado.getRect(), buildings.get(i).getRect())) {
                if(buildings.get(i).getVisibility()) {
                    buildings.get(i).setExploding(true);
                }
                buildings.get(i).setInvisible();
            }
        }
    }

    public void draw() {
        //Make sure surface is valid, dont know what this means but program crashes if you don't
        if(ourHolder.getSurface().isValid()) {
            //this locks the drawing surface, and sets the canvas equal to the drawing surface
            canvas = ourHolder.lockCanvas();
            //sets background color
            canvas.drawColor(Color.argb(255, 0, 0, 0));
            //sets paint color
            paint.setColor(Color.argb(255,153,153,153));
            //draws circle
            canvas.drawCircle((float) (this.getWidth()*0.85), (float) (this.getHeight()*0.75), 100, paint);
            //updates paint color
            paint.setColor(Color.argb(255, 255, 255, 255));
            //draws other circle
            canvas.drawCircle((float)circleXPosition, (float)circleYPosition, 60, paint);
            //draw a tornado bitmap
            canvas.drawBitmap(tornado.getBitmap(), tornado.getX(), tornado.getY(), paint);
            //draw building if visible
            for(int i = 0; i < buildings.size(); i++) {
                if(buildings.get(i).getVisibility()) {
                    canvas.drawBitmap(buildings.get(i).getBitmap(), buildings.get(i).getX(), buildings.get(i).getY(), paint);
                }
                //draw animated explosion

                if(buildings.get(i).isExploding()) {
                    generateExplosion(buildings.get(i));
                }
            }
            //unlocks the canvas, which i think means the OS can draw to it again. It also posts what we've drawn to the actual screen, i think.
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void generateExplosion(Building building) {
        if(explosionFrame == 1) {
            canvas.drawBitmap(explosion1, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        }  else if(explosionFrame == 2) {
            canvas.drawBitmap(explosion2, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 3) {
            canvas.drawBitmap(explosion3, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 4) {
            canvas.drawBitmap(explosion4, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 5) {
            canvas.drawBitmap(explosion5, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 6) {
            canvas.drawBitmap(explosion6, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 7) {
            canvas.drawBitmap(explosion7, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 8) {
            canvas.drawBitmap(explosion8, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 9) {
            canvas.drawBitmap(explosion9, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 10) {
            canvas.drawBitmap(explosion10, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 11) {
            canvas.drawBitmap(explosion11, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else if(explosionFrame == 12) {
            canvas.drawBitmap(explosion12, (float)(building.getX()-200), (float)(building.getY()*1.8), paint);
        } else {
            building.setExploding(false);
            explosionFrame = 0;
        }
        explosionFrame++;
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
