package fr.iut.random_box;

public class Box {
    public Box(){}

    //get random number between 0 & 100
    public static int getRandomNumber(){
        return (int) (Math.random()*100);
    }
    //get random number between a interval
    public static int getRandomNumber(int min, int max){
        return (int) (Math.random()*(max-min))+min;
    }
}
