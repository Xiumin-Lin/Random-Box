package fr.iut.random_box.boxes;

import android.view.View;

import org.json.JSONObject;

import fr.iut.random_box.InfoActivity;
import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class AnimeBox extends RandomBox {
    public AnimeBox() {
        super("anime");
//        super("anime","https://cdn.myanimelist.net/images/anime/13/50521.jpg",true); //TODO
    }

    @Override
    public void setPopupView(View popupView) {
        super.setPopupView(popupView);
        String imageUri = "https://cdn.myanimelist.net/images/anime/13/50521.jpg"; //TODO
        setImgViewURL(popupView.findViewById(R.id.imgPopContent), imageUri);

        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Hyouka");
        setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), "氷菓");

        View scrollView = popupView.findViewById(R.id.scrollPopDetail);
        scrollView.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void setPopupView(View popupView, JSONObject json) throws JSONException {
//        super.setPopupView(popupView, json);
//        String imageUri = "https://cdn.myanimelist.net/images/anime/13/50521.jpg";
//        setImgViewURL(popupView.findViewById(R.id.imgPopContent), imageUri);
//
//        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), "Hyouka");
//        setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), "氷菓");
//
//        View scrollView = popupView.findViewById(R.id.scrollPopDetail);
//        scrollView.setVisibility(View.VISIBLE);
//    }
}
