package fr.iut.random_box;

import android.graphics.Color;

import java.util.Random;

public class RandomBox {
    public RandomBox(){}

    //get random number between 0 & 100
    public static int getRandomNumber(){ return (new Random()).nextInt(101); }
    //get random number between a interval
    public static int getRandomNumber(int min, int max){
        return min + (new Random()).nextInt(max-min);
    }

    public static int getRandomColor(){
        //TODO Post request to http://thecolorapi.com/id?rgb=0,71,171&format=json
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


}
