package fr.iut.random_box;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomBox {
    public RandomBox(){}

    /**
     * @return a random number between 0 & 100
     */
    public static int getRandomNumber(){ return (new Random()).nextInt(101); }

    public static int getRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public static ArrayList<String> getAllBoxName(){
        ArrayList<String> boxNameList = new ArrayList<>();
        DatabaseReference dbBox = FirebaseDatabase.getInstance().getReference("box");
        dbBox.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()){
                    boxNameList.add(child.child("name").getValue().toString());
                }
                Log.d("rb", boxNameList.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                boxNameList.addAll(Arrays.asList("number","meal","color","movie"));
            }
        });

        return boxNameList;
    }

    private static void setTextViewContent(TextView textViewById, String text) {
        textViewById.setText(text);
        textViewById.setVisibility(View.VISIBLE);
    }

    private static void setImgViewURL(ImageView textViewById, String url) {
        Picasso.get().load(url).error(R.drawable.btn_blackheart).into(textViewById);
        textViewById.setVisibility(View.VISIBLE);
    }

    public static void setPopupNumber(View popupView) {
        Log.d("rb", "Set Number Box");
        setTextViewContent(popupView.findViewById(R.id.txtPopBigNumber), "" + RandomBox.getRandomNumber());
    }

    public static void setPopupColor(View popupView) {
        Log.d("rb", "Set Color Box");
        ImageView imgRandColor = popupView.findViewById(R.id.imgPopContent);

        //convert px to dp
        int _200dpInpx = (int) (200 * popupView.getResources().getDisplayMetrics().density);
        Log.d("rb", "200 dp = " + _200dpInpx + " px");
        imgRandColor.getLayoutParams().height = _200dpInpx; //height receive px
        imgRandColor.getLayoutParams().width = _200dpInpx;

        int randColor = RandomBox.getRandomColor();
        imgRandColor.setBackgroundColor(randColor);
        imgRandColor.setVisibility(View.VISIBLE);

        //convert int color to hex format string
        String hex = "HEX : " + String.format("#%06X", (0xFFFFFF & randColor));
        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), hex);
    }

    public static void setPopupMovie(View popupView) {
        String url = "https://m.media-amazon.com/images/M/MV5BNjM0NTc0NzItM2FlYS00YzEwLWE0YmUtNTA2ZWIzODc2OTgxXkEyXkFqcGdeQXVyNTgwNzIyNzg@._V1_SX300.jpg";
        setImgViewURL(popupView.findViewById(R.id.imgPopContent), url);

        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Guardians of the Galaxy Vol. 2");
    }

    public static void setPopupMeal(View popupView, JSONObject response) throws JSONException {
        //Popup
        String titleMeal = response.getJSONArray("meals").getJSONObject(0).getString("strMeal");
        String categMeal = response.getJSONArray("meals").getJSONObject(0).getString("strCategory");
        String areaMeal = response.getJSONArray("meals").getJSONObject(0).getString("strArea");
        String imgUrl = response.getJSONArray("meals").getJSONObject(0).getString("strMealThumb");

        if(!TextUtils.isEmpty(imgUrl)){
            setImgViewURL(popupView.findViewById(R.id.imgPopContent), imgUrl);
        }
        setTextViewContent(popupView.findViewById(R.id.txtPopTitle), titleMeal);
        setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), categMeal);
        setTextViewContent(popupView.findViewById(R.id.txtPopContentInfo1), areaMeal);
    }

    public static void setPopupAnime(View popupView) {
        String imageUri = "https://cdn.myanimelist.net/images/anime/13/50521.jpg";
        setImgViewURL(popupView.findViewById(R.id.imgPopContent), imageUri);

        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Hyouka");
        setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), "氷菓");

        View scrollView = popupView.findViewById(R.id.scrollPopDetail);
        scrollView.setVisibility(View.VISIBLE);
    }

    public static void setPopupNASA(View popupView) {
        ImageView imgContent = popupView.findViewById(R.id.imgPopContent);
        String imageUri = "https://apod.nasa.gov/apod/image/2103/M16Ir_HubbleRomero_2786.jpg";
        Picasso.get().load(imageUri).error(R.drawable.btn_blackheart).into(imgContent);
        imgContent.setVisibility(View.VISIBLE);

        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Pillars of the Eagle Nebula in Infrared");
    }


}
