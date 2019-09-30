package com.example.snakegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.view.MotionEvent;
import android.media.SoundPool;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import  java.io.InvalidObjectException;

public class SnakeGame extends SurfaceView implements Runnable {

    //Obj for the game loop/thread
    private Thread mThread = null;

    //Control pausing between updates
    private  long mNextFrameTime;

    private volatile boolean mPlaying = false;
    private volatile boolean mPaused = true;

    //Sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    //The size in Segments of Playable area
    private  final int NUM_BLOCKS_WIDE = 40;
    private  int mNumBlocksHigh;

    //How many points the player have
    private int mScore;

    //Object for Drawing
    private Canvas mCasvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;

    //The Snake
    private  Snake mSnake;
    //And the apple
    private Apple mApple;

    public SnakeGame(Context context, Point size) {
        super(context);

        int blockSize = size.x / NUM_BLOCKS_WIDE;
        //How many blocks of the same size will fit in Ht
        mNumBlocksHigh = size.y / blockSize;

        //Init the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            mSP = new SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build();
        } else{
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }
        try{
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            //Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);
        }catch(IOException e){
            //Error

        }

        mSurfaceHolder = getHolder();
        mPaint = new  Paint();

        //Snake snake = new Snake();
        Snake mSnake = new Snake(context, new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);
        //Apple apple = new Apple();
        Apple mApple = new Apple(context,new Point(NUM_BLOCKS_WIDE, mNumBlocksHigh), blockSize);



    }



    @Override
    public void run() {
        while (mPlaying){
            if (!mPlaying){
                //Update 10 a sec
                if (updateRequired()){
                    update();
                }
            }
            draw();
        }
    }

    public void newGame(){
        //reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        //Get the apple ready for Dinner
        mApple.spawn();

        //Reset the Score
        mScore = 0;

        //Setup mNextFrameTime so update can trigger
        mNextFrameTime = System.currentTimeMillis();
    }

    public boolean updateRequired(){

        //Run
         final long TARGET_FPS = 10;
         //There are 1000 milliseconds in second
        final long MILLIS_PER_SECOND = 1000;

        //Are we due to update the
        if (mNextFrameTime<= System.currentTimeMillis()){
            //Tenth of a second has passed
            //Setup when the next update will be trigger
            mNextFrameTime =  System.currentTimeMillis() + MILLIS_PER_SECOND / TARGET_FPS;

        }
        return true;
    }
     public void update(){

        //Move the snake
         mSnake.move();

         //Did snake eat the apple
         if (mSnake.detectDeath()){
             mSP.play(mCrashID, 1, 1, 0, 0,1);

             mPaused = true;
         }

     }
     public void draw(){
        //Get a lock on hte mCanvas

         if(mSurfaceHolder.getSurface().isValid()){
             mCasvas = mSurfaceHolder.lockCanvas();

             //Fill the Screen with color
             mCasvas.drawColor(Color.argb(225, 26, 128, 182));

             //Set the size and color of the mPaint for the text
             mPaint.setColor(Color.argb(255, 255, 255,255));
             mPaint.setTextSize(120);

             //Draw the score
             mCasvas.drawText("" + mScore, 20, 120, mPaint);

             //Draw the apple and the snake
             mApple.draw(mCasvas, mPaint);
             mSnake.draw(mCasvas,mPaint);

             //Draw some text while paused
             if (mPaused){
                 //Set the size and color of mPaint for text
                 mPaint.setColor(Color.argb(255, 255, 255, 255));
                 mPaint.setTextSize(250);

                 //Draw the message
                 //Upgrade soon
                 //mCasvas.drawText("Tap to pay!", 200, 700, mPaint);
                 mCasvas.drawText(getResources().getString(R.string.tap_to_play), 200, 700, mPaint);

                 //Unlock the canvas to show graphics for this frame
                 mSurfaceHolder.unlockCanvasAndPost(mCasvas);
             }
         }
     }
     @Override
    public  boolean onTouchEvent(MotionEvent motionEvent){
        switch (motionEvent.getAction() &MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (mPaused) {
                    mPaused = false;
                    newGame();

                    //Dont want to process snake
                    //Direction for this tap
                    return true;
                }
                //Let Snake Class handle
                mSnake.switchHeading(motionEvent);
                break;

            default:
                break;
        }
        return  true;
        }

        //Stop the Thread
    public  void pause(){
        mPlaying = false;
        try{
            mThread.join();
        }catch (InterruptedException e){
            //Error
        }
    }

    //Start thread
    public void resume(){
        mPlaying = true;
        mThread = new Thread(this);
        mThread.start();

    }


     }

