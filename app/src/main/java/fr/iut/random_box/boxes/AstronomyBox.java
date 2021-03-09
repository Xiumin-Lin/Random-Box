package fr.iut.random_box.boxes;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class AstronomyBox extends RandomBox {
    public AstronomyBox() {
        super("astronomy", "https://api.nasa.gov/planetary/apod?api_key=Y9Sgb5jB9MEdU6PLamtugrPCJ53cMSlHd2bUmSl9&count=1", false);
    }

    @Override
    public void setPopupView(View popupView, JSONObject json) throws JSONException {
        super.setPopupView(popupView, json);
        String imgUrl = json.getString("url");
        String title = json.getString("title");
        String date = json.getString("date");
        if(json.has("copyright")){ //the copyright is not always here
            String copyright = json.getString("copyright");
            setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), "(c) : " + copyright);
        }
        setImgViewURL(popupView.findViewById(R.id.imgPopContent), imgUrl);
        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), title);
        setTextViewContent(popupView.findViewById(R.id.txtPopContentInfo1), date);
    }

    @Override
    public void setInfoView(View infoView, JSONObject json) {
        super.setInfoView(infoView, json);
    }
}
