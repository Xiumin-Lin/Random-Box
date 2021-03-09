package fr.iut.random_box;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
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
import java.util.Random;

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
     * @param popupView
     */
    public void setPopupView(View popupView){
        Log.d("rb", "Set " + name + " Box in Popup View");
    }

    /**
     * Set popup view for box which require API to work
     * @param popupView
     * @param json the json response on success of the request to the API
     */
    public void setPopupView(View popupView, JSONObject json) throws JSONException {
        Log.d("rb", "Set " + name + " Box in Popup View");
    }

    /**
     * Set Info activity for box which require API
     * @param infoView the InfoActivity
     * @param json the json response on success of the request to the API
     */
    public void setInfoView(View infoView, JSONObject json){
        if(!needAPI()){
            Log.d("rb", name + " Box don't have more info");
            return;
        }
        Log.d("rb", "Set " + name + " Box in Info Activity");
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

    public static void setTextViewContent(TextView textViewById, String text) {
        textViewById.setText(text);
        textViewById.setVisibility(View.VISIBLE);
    }

    public static void setImgViewURL(ImageView textViewById, String url) {
        Picasso.get().load(url).error(R.drawable.btn_blackheart).into(textViewById);
        textViewById.setVisibility(View.VISIBLE);
    }
}
