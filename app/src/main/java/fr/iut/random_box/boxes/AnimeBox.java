package fr.iut.random_box.boxes;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class AnimeBox extends RandomBox {
    //Ban list of nsfw genre id
    private static final ArrayList<Integer> BAN_GENRE_ID = new ArrayList<>(Arrays.asList(12,33,34));
    private static final int NB_GENRE = 43;
    public enum a{A, V}

    /**
     * Create a AnimeBox ou a MangaBox depending of the given BoxType
     * @param name has BoxType.ANIME or BoxType.MANGA
     * @param apiUrl apiUrl the api url where we can get information
     */
    public AnimeBox(BoxType name, String apiUrl) {
        super(name.toString().toLowerCase(),apiUrl,true);
    }

    @Override
    public void setPopupView(View popupView, JSONObject json) throws JSONException {
        super.setPopupView(popupView, json);

        setImgViewURL(popupView.findViewById(R.id.imgPopContent), json.getString("image_url"));
        setTextViewContent(popupView.findViewById(R.id.txtPopContentTitle), json.getString("title"));
        setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), json.getString("type"));
    }

    /**
     * @return a random Anime Genre Id for the request url of the api
     */
    private int getRandomGenreId(){
        int randAnimeGenreId;
        do{ //get a genre id that is not ban
            randAnimeGenreId = NumberBox.getRandomNumber(1, NB_GENRE);
        } while (BAN_GENRE_ID.contains(randAnimeGenreId));
        return randAnimeGenreId;
    }

    /**
     * @return the anime/manga api url with a random genre id
     */
    @Override
    public String getApiUrl() {
        return super.getApiUrl() + "/" + getRandomGenreId();
    }

    @Override
    public JsonObjectRequest makeJsonObjectRequest(View popupView, Intent infoActivity) {
        return new JsonObjectRequest(Request.Method.GET, getApiUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //response return a JSONObject containing a big JSONArray of anime/manga data
                    //then we randomly choose a anime/manga from this JSONArray
                    int randIdx = NumberBox.getRandomNumber();
                    JSONObject randomAnimeManga = response.getJSONArray(name).getJSONObject(randIdx);
                    infoActivity.putExtra("jsonResponse", randomAnimeManga.toString());
                    Log.d("rb", name + " JSON = " + randomAnimeManga.toString());
                    setPopupView(popupView, randomAnimeManga);
                } catch (JSONException e) {
                    Log.e("rb", name + " JSONObject Error : " +  e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("rb", name + " JSONObject Request FAIL : " + error.getMessage());
            }
        });
    }
}
