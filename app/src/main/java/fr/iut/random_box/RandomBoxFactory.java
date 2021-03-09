package fr.iut.random_box;

import android.util.Log;

import fr.iut.random_box.boxes.*;

public class RandomBoxFactory {
    public static RandomBox buildBox(String box_name) {
        switch (box_name){
            case "number": return new NumberBox();
            case "color": return new ColorBox();
            case "movie": return new MovieBox();
            case "meal": return new MealBox();
            case "anime": return new AnimeBox();
            case "astronomy": return new AstronomyBox();
            default:
                Log.d("rb", "Unknown box, can't build " + box_name + " box");
                return null;
        }
    }
}
