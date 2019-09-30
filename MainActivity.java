package com.example.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class MainActivity extends AppCompatActivity {

    //Declare an instance of SnakeGame
    SnakeGame mSnakeGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get pixels dimensions
        Display display = getWindowManager().getDefaultDisplay();

        //Init the results into a point obj
        Point size = new Point();
        display.getSize(size);

        //Create a new instance of the snake game
        mSnakeGame = new SnakeGame(this, size);

        //Make Snake engine
        setContentView(mSnakeGame);

    }
    protected void onResume(){
        super.onResume();
        mSnakeGame.resume();

    }
    protected void onPause(){
        super.onPause();
        mSnakeGame.pause();

    }
}
