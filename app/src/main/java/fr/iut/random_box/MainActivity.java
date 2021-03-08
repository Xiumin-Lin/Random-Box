package fr.iut.random_box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference db;
    private MainActivity activity;
    private static final int NB_MAX_BOX = 6;
    private ArrayList<String> listBox = new ArrayList<>(Arrays.asList("number","meal","color","movie","anime","nasa"));;
    private final ArrayList<Integer> boxIdList = new ArrayList<>(Arrays.asList(R.id.box_1,R.id.box_2,R.id.box_3,R.id.box_4,R.id.box_5,R.id.box_6));

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private MediaPlayer mpRollDice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.db = FirebaseDatabase.getInstance().getReference();
        this.activity = this;

        this.mpRollDice = MediaPlayer.create(this, R.raw.roll_dice);
        shuffleBox();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    /**
     * Shows in a popup the content of the box
     * @param view : the view that called the method
     */
    public void onClickBtnBox(View view){
        String box_name = "";
        if(boxIdList.size() == 0)
            return;
        for(int i = 0; i < boxIdList.size(); i++){
            if(view.getId() == boxIdList.get(i)){
                box_name += listBox.get(i); break;
            }
            if(i == boxIdList.size() - 1){
                Log.d("rb", "Unknown box id"); return;
            }
        }

        //increment stat of the selected box
        db.child("stats").child("box").child(box_name).setValue(ServerValue.increment(1));

        //create & set intent
        Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
        infoActivity.putExtra("box_name", box_name);

        //create box popup builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //activity

        // set the custom layout
        final View popupView = getLayoutInflater().inflate(R.layout.popup,null);
        builder.setView(popupView);

        //update custom layout components
        TextView title = popupView.findViewById(R.id.txtPopTitle);
        title.setText(box_name);

        switch (box_name){
            case "number":
                RandomBox.setPopupNumber(popupView); break;
            case "color":
                RandomBox.setPopupColor(popupView); break;
            case "movie":
                RandomBox.setPopupMovie(popupView); break;
            case "meal":
                String api_url ="https://www.themealdb.com/api/json/v1/1/random.php";
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Intent
                            infoActivity.putExtra("jsonResponse", response.getJSONArray("meals").getJSONObject(0).toString());
                            RandomBox.setPopupMeal(popupView, response);
                        } catch (JSONException e) {
                            title.setText("Error in loading Meal data");
                            Log.e("rb", "JSON Error : " +  e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        title.setText("That didn't work!");
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest); break;
            case "anime": RandomBox.setPopupAnime(popupView); break;
            case "nasa": RandomBox.setPopupNASA(popupView); break;
        }
        if(!box_name.equals("number") && !box_name.equals("color")){
            builder.setNeutralButton("Get More Info", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(infoActivity);
                    activity.finish();
                }
            });
        }

        //display popup
        AlertDialog popup = builder.create();
        popup.show();
    }

    /**
     * Shuffles the boxes position by clicking on the logo.
     * @param view : the view that called the method
     */
    public void onClickLogo(View view){
        shuffleBox();
    }

    /**
     * Shuffles the boxes position
     */
    public void shuffleBox(){
        //Makes a rolling dice sound
        Log.d("rb", "Rolling Dice");
        mpRollDice.start();

        //
        Log.d("rb", "Shuffle All Box");
        Collections.shuffle(listBox);

        Button btnBox;
        for(int i = 0; i < NB_MAX_BOX; i++){
            btnBox = findViewById(boxIdList.get(i));
            btnBox.setText(listBox.get(i));
        }
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        /**
         * Detects the shake motion and call shuffleBox()
         * @param se : the SensorEvent passed in parameter
         */
        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 20) {
                Log.d("rb", "Device shaken");
                shuffleBox();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // ignore
        }
    };

    @Override
    protected void onResume() {
        // Activates the accelerometer
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Deactivates the accelerometer to saves resources
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

}