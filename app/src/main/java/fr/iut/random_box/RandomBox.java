package fr.iut.random_box;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class RandomBox {
    public static String name = "randomBox";
    private String apiUrl = "";
    private Boolean hasJsonObject = true;

    /**
     * The box no need api to work
     */
    public RandomBox(String name){
        RandomBox.name = name;
    }

    public RandomBox(String name, String apiUrl, Boolean hasJsonObject){
        this(name);
        this.apiUrl = apiUrl;
        if(!apiUrl.isEmpty()){
            this.hasJsonObject = hasJsonObject;
        }
    }

    public Boolean needAPI(){
        return !apiUrl.isEmpty();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public Boolean hasJsonObject(){
        return hasJsonObject;
    }

    /**
     * Set popup view for box which don't need API
     * @param popupView the popupView to setup
     */
    public void setPopupView(View popupView){
        Log.d("rb", "Set " + name + " Box in Popup View");
    }

    /**
     * Set popup view for box which require API to work
     * @param popupView the popupView to setup
     * @param json the json response on success of the request to the API
     */
    public void setPopupView(View popupView, JSONObject json) throws JSONException {
        Log.d("rb", "Set " + name + " Box in Popup View");
    }

    /**
     * Set Info activity for box which require API
     * @param infoView the InfoActivity
     * @param json the json response on success of the request to the API
     * @param generalFields map of general box fields name obtained via the database to show in Info activity //TODO make a better description
     * @param additionalFields map of additional box fields name obtained via the database that will be display in the listView
     */
    public void setInfoView(InfoActivity infoView, JSONObject json, HashMap<String, String> generalFields, HashMap<String, String> additionalFields) throws JSONException {
        if(!needAPI()){
            Log.d("rb", name + " Box don't have more info");
            return;
        }
        Log.d("rb", "Set " + name + " Box in Info Activity");

        for(Map.Entry<String,String> pair: generalFields.entrySet()) {
            switch (pair.getKey()){
                case "title":
                    String title = json.getString(pair.getValue());
                    TextView titleView = infoView.findViewById(R.id.txtInfoContentTitle);
                    titleView.setText(title); break;
                case "subtitle":
                    String subtitle = json.getString(pair.getValue());
                    TextView subtitleView = infoView.findViewById(R.id.txtInfoContentSubtitle);
                    setTextViewContent(subtitleView, subtitle); break;
                case "description":
                    String longText = json.getString(pair.getValue());
                    TextView longtextView = infoView.findViewById(R.id.txtInfoContentLong);
                    setTextViewContent(longtextView, longText);
                    break;
                case "picture":
                    String imgUrl = json.getString(pair.getValue());
                    ImageView imgView = infoView.findViewById(R.id.imgInfoContent);
                    setImgViewURL(imgView, imgUrl);
                    break;
                default:
                    Log.d("rb", pair.getKey() + " key doesn't exist in generalFields");
            }
        }

        //set list view of optional info
        ListView infoItems = infoView.findViewById(R.id.listInfo);
        HashMap<String,String> itemList = new HashMap<>();
        for(Map.Entry<String,String> pair: additionalFields.entrySet()) {
            itemList.put(pair.getKey(),json.getString(pair.getValue()));
        }
        if(!itemList.isEmpty()){
            infoItems.setAdapter(new BoxInfoItemAdapter(infoView, itemList));
            infoItems.setVisibility(View.VISIBLE);
        }
    }

    public JsonObjectRequest makeJsonObjectRequest(View popupView, Intent infoActivity){
        return new JsonObjectRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Intent
                    infoActivity.putExtra("jsonResponse", response.toString());
                    Log.d("rb", name + " JSON = " + response.toString());
                    setPopupView(popupView, response);
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

    public JsonArrayRequest makeJsonArrayRequest(View popupView, Intent infoActivity){
        return new JsonArrayRequest(Request.Method.GET, apiUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    //Intent
                    infoActivity.putExtra("jsonResponse", response.getJSONObject(0).toString());
                    Log.d("rb", name + " JSON = " + response.getJSONObject(0).toString());
                    setPopupView(popupView, response.getJSONObject(0));
                } catch (JSONException e) {
                    Log.e("rb", name + " JSONArray Error : " +  e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("rb", name + " JSONArray Request FAIL : " + error.getMessage());
            }
        });
    }

    public static void setTextViewContent(TextView textViewById, String text) {
        textViewById.setText(text);
        textViewById.setVisibility(View.VISIBLE);
    }

    public static void setImgViewURL(ImageView textViewById, String url) {
        if (TextUtils.isEmpty(url)){
            Log.d("rb", "The url is empty, can't set ImgView");
            return;
        }
        Picasso.get().load(url).error(R.drawable.btn_blackheart).into(textViewById);
        textViewById.setVisibility(View.VISIBLE);
    }
}
