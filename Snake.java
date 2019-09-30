package com.example.snakegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

public class Snake {

    private ArrayList<Point> segmentLocations;

    //How big is each srgment of the snake
    private int mSegmentSize;

    //How big id the entire grid
    private  Point mMoveRange;

    //Where is the center of the screen
    private int halfWayPoint;

    //For tracking movement Heading
    private enum Heading{
        UP, RIGHT, DOWN, LEFT,
    }

    //Start by heading to the right
    private Heading heading = Heading.RIGHT;

    //BitMap for each direction
    private Bitmap mBitmapHeadRight;
    private  Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private  Bitmap mBitmapHeadDown;

    //A bitmap for the body
    private  Bitmap mBitmapBody;

    Snake(Context context, Point mr, int ss){
        //Init our ArrayList
        segmentLocations = new ArrayList<>();

        //Init the segment size and movement
        mSegmentSize = ss;
        mMoveRange = mr;

        //Crreate and Scale the bitmap
        mBitmapHeadRight = BitmapFactory.decodeResource(context.getResources(),R.drawable.head);

        //Create 3 more
        mBitmapHeadLeft = BitmapFactory.decodeResource(context.getResources(),R.drawable.head);
        mBitmapHeadUp = BitmapFactory.decodeResource(context.getResources(),R.drawable.head);
        mBitmapHeadDown = BitmapFactory.decodeResource(context.getResources(),R.drawable.head);

        //Mosify the bitmaps to face the snake head
        mBitmapHeadRight = Bitmap.createScaledBitmap(mBitmapHeadRight, ss, ss, false);

        //A matrix for scaling
        Matrix matrix = new Matrix();
        matrix.preScale(-1,1);
        mBitmapHeadLeft = Bitmap.createBitmap(mBitmapHeadRight,0,0,ss,ss,matrix,true);

        //A matrix for rotating
        matrix.postRotate(-90);
        mBitmapHeadUp = Bitmap.createBitmap(mBitmapHeadRight,0,0,ss,ss,matrix,true);


        matrix.postRotate(180);
        mBitmapHeadDown = Bitmap.createBitmap(mBitmapHeadRight,0,0,ss,ss,matrix,true);

        mBitmapBody = BitmapFactory.decodeResource(context.getResources(),R.drawable.body);

        mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody,ss,ss,false);

        //The Halfway point across the screen
        halfWayPoint = mr.x * ss / 2;


    }
    void reset(int w, int h){
        //Reset he heading
        heading = Heading.RIGHT;

        //Delete the old contents of the Arraylist
        segmentLocations.clear();

        //Start with a single snake segment
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    void move(){
        //Move the body
        //start at the back and move it

        for (int i = segmentLocations.size() - 1; i > 0; i--){
            //Make it the same value as the next segment
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;

            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }

        //Move the head in the appropriate
        //Get the existing head position
        Point p = segmentLocations.get(0);

        //Move it appropriately
        switch (heading){
            case UP:
                p.y--;
                break;
            case RIGHT:
                p.x++;
                break;
            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
        }

        //Insert the adjuzted point back into position 0
        segmentLocations.set(0, p);
    }
     boolean detectDeath(){
        //Has the snake died?
         boolean dead = false;

         //Hit any of the screen edges
         if(segmentLocations.get(0).x == -1 || segmentLocations.get(0).x > mMoveRange.x || segmentLocations.get(0).y == -1 || segmentLocations.get(0).y > mMoveRange.y){

             dead = true;
         }

         //Eaten itself
         for(int i = segmentLocations.size() - 1; i > 0;i--){
             //Have any of the sectiion collided with the head
             if (segmentLocations.get(0).x == segmentLocations.get(i).x && segmentLocations.get(0).y == segmentLocations.get(i).y){
                 dead = true;
             }
         }

         return dead;
     }

     boolean checkDinner(Point One){

        if(segmentLocations.get(0).x == One.x && segmentLocations.get(0).y == One.y){

            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
     }

     void draw(Canvas canvas, Paint paint){

        if (!segmentLocations.isEmpty()){

            //Draw Head
            switch(heading){
                case RIGHT:
                canvas.drawBitmap(mBitmapHeadRight, segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);
                break;

                case LEFT:
                    canvas.drawBitmap(mBitmapHeadLeft, segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);
                    break;

                case UP:
                    canvas.drawBitmap(mBitmapHeadUp, segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);
                    break;

                case DOWN:
                    canvas.drawBitmap(mBitmapHeadDown, segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
            }

            //Draw the snake body one blick at a time
            for (int i = 1; i < segmentLocations.size(); i++){
                canvas.drawBitmap(mBitmapBody, segmentLocations.get(0).x * mSegmentSize, segmentLocations.get(0).y * mSegmentSize, paint);

            }
        }
     }

     //Handle changing direction
    void switchHeading(MotionEvent motionEvent){

        //Right hand sided tap
        if (motionEvent.getX() >= halfWayPoint){
            switch (heading){
                //Rotate Right
                case UP:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.DOWN;
                case DOWN:
                    heading = Heading.LEFT;
                case LEFT:
                    heading = Heading.UP;
            }
        }else {
            //Rotate Left
            switch (heading){
                case UP:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.DOWN;
                case DOWN:
                    heading = Heading.RIGHT;
                case RIGHT:
                    heading = Heading.UP;
                    break;
            }
        }
    }
}

