package com.example.snakegame;
import android.content.Context;
import android.graphics.Bitmap;
import  android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import  java.util.Random;

public class Apple {

    //Location of the apple on the grid
    //Not in Pixel
    private Point mLocation = new Point();

    //The range of values we choose for an apple
    private Point mSpawnRange;
    private int mSize;

    //An image to rep the apple
    private Bitmap mBipmapApple;

    Apple(Context context, Point sr, int s){
        //Make a note of the passed in spawn range
        mSpawnRange = sr;

        //Make a note apple size
        mSize = s;

        //Hide the apple off-screen until game str
        mLocation.x = -10;

        //Load images of to the bitmap
        mBipmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        //Resize the Bitmap
        mBipmapApple = Bitmap.createScaledBitmap(mBipmapApple, s, s, false);

    }

    void spawn(){
        //Choose two random values and place the apple
        Random random = new Random();
        mLocation.x = random.nextInt(mSpawnRange.x) + 1;
        mLocation.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    //Let the game know where the apple is
    Point getLocation(){ return mLocation; }

    void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBipmapApple, mLocation.x * mSize, mLocation.y * mSize, paint);
    }

}
