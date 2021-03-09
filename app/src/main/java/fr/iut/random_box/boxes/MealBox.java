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

import fr.iut.random_box.R;
import fr.iut.random_box.RandomBox;

public class MealBox extends RandomBox {
    public MealBox() {
        super("meal", "https://www.themealdb.com/api/json/v1/1/random.php", true);
    }

    @Override
    public void setPopupView(View popupView, JSONObject json) throws JSONException {
        super.setPopupView(popupView);
        String imgUrl = json.getString("strMealThumb");
        String title = json.getString("strMeal");
        String category = json.getString("strCategory");
        String area = json.getString("strArea");

        setImgViewURL(popupView.findViewById(R.id.imgPopContent), imgUrl);
        setTextViewContent(popupView.findViewById(R.id.txtPopTitle), title);
        setTextViewContent(popupView.findViewById(R.id.txtPopSubTitle), category);
        setTextViewContent(popupView.findViewById(R.id.txtPopContentInfo1), area);
    }

    @Override
    public void setInfoView(View infoView, JSONObject json) {
        super.setInfoView(infoView, json);
    }

    @Override
    public JsonObjectRequest makeJsonObjectRequest(View popupView, Intent infoActivity) {
        return new JsonObjectRequest(Request.Method.GET, getApiUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //meal json structure need to precise "meal" to work
                    infoActivity.putExtra("jsonResponse", response.getJSONArray("meals").getJSONObject(0).toString());
                    Log.d("rb", name + " JSON = " + response.getJSONArray("meals").getJSONObject(0).toString());
                    setPopupView(popupView, response.getJSONArray("meals").getJSONObject(0));
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
