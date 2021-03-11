package fr.iut.random_box;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private static HashMap<String, String> statsList = new HashMap<>();
    /**
     * retrieves the statistics from the database
     */
    public static void updateStats(){

        DatabaseReference dbBox = FirebaseDatabase.getInstance().getReference("stats/box");
        dbBox.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statsList.clear();
                for(DataSnapshot child: snapshot.getChildren()){
                    statsList.put(child.getKey(), child.getValue().toString());
                }
                Log.d("rb", statsList.toString()+"data change");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("rb", "getStats cancelled : "+error.getMessage());
            }
        });
    }
    /**
     * Returns the statistics of the boxes
     */
    public static HashMap<String, String> getStats() {
        return statsList;
    }
}
