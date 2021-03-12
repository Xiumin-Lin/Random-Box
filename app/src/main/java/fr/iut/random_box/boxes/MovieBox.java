package fr.iut.random_box.boxes;

import android.view.View;

import org.json.JSONObject;

import fr.iut.random_box.InfoActivity;
import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class MovieBox extends RandomBox {
    public MovieBox() {
        super("movie");
//        super("movie", "http://www.omdbapi.com/?i=tt3896198&apikey=ea4382be", true); //TODO
    }

    @Override
    public void setPopupView(View popupView){
        super.setPopupView(popupView);
        String imageUri = "https://m.media-amazon.com/images/M/MV5BNjM0NTc0NzItM2FlYS00YzEwLWE0YmUtNTA2ZWIzODc2OTgxXkEyXkFqcGdeQXVyNTgwNzIyNzg@._V1_SX300.jpg"; //TODO
        setImgViewURL(popupView.findViewById(R.id.imgPopContent), imageUri);
        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Guardians of the Galaxy Vol. 2");
    }

//    @Override
//    public void setPopupView(View popupView, JSONObject json) throws JSONException {
//        super.setPopupView(popupView, json);
//
//        setImgViewURL(popupView.findViewById(R.id.imgPopContent), getApiUrl());
//        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Guardians of the Galaxy Vol. 2");
//    }
}
