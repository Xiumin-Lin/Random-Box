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
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private Button btnN;
    private DatabaseReference db;
    private MainActivity activity;
    private static final int NUMBERBOX = 6;
    ArrayList<String> listBox;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.db = FirebaseDatabase.getInstance().getReference();
        this.activity = this;
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

        switch (view.getId()){
            case R.id.box_1: box_name += listBox.get(0); break;
            case R.id.box_2: box_name += listBox.get(1); break;
            case R.id.box_3: box_name += listBox.get(2); break;
            case R.id.box_4: box_name += listBox.get(3); break;
            default: Log.d("rb", "Unknown box id"); return;
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
                TextView txtRandNumber = popupView.findViewById(R.id.txtPopBigNumber);
                String randNum = "" + RandomBox.getRandomNumber();
                txtRandNumber.setText(randNum);
                txtRandNumber.setVisibility(View.VISIBLE); break;
            case "color":
                ImageView imgRandColor = popupView.findViewById(R.id.imgPopContent);
                //convert px to dp
                int _200dpInpx = (int) (200 * popupView.getResources().getDisplayMetrics().density);
                Log.d("rb", "200 dp = " + _200dpInpx + " px");
                imgRandColor.getLayoutParams().height = _200dpInpx; //height receive px
                imgRandColor.getLayoutParams().width = _200dpInpx;
                imgRandColor.setVisibility(View.VISIBLE);

                int randColor = RandomBox.getRandomColor();
                imgRandColor.setBackgroundColor(randColor);
                TextView txtCTitle = popupView.findViewById(R.id.txtPopContentTitle);
                String hex = "HEX = " + String.format("#%06X", (0xFFFFFF & randColor));
                txtCTitle.setText(hex); //convert int color to hex format
                txtCTitle.setVisibility(View.VISIBLE); break;
            case "movie":
                ImageView imgContent = popupView.findViewById(R.id.imgPopContent);
                String imageUri = "https://cdn.myanimelist.net/images/anime/13/50521.jpg";
//                LoadImage loadImage = new LoadImage(imgContent);
//                loadImage.execute(url);
                Picasso.get().load(imageUri).error(R.drawable.btn_blackheart).into(imgContent);
                imgContent.setVisibility(View.VISIBLE);

                TextView imgTitle = popupView.findViewById(R.id.txtPopContentTitle);
                imgTitle.setText("Hyouka");
                imgTitle.setVisibility(View.VISIBLE);

                TextView subTitle = popupView.findViewById(R.id.txtPopSubTitle);
                subTitle.setText("氷菓");
                subTitle.setVisibility(View.VISIBLE);

                View scrollView = popupView.findViewById(R.id.scrollPopDetail);
                scrollView.setVisibility(View.VISIBLE); break;
            case "meal":
                String api_url ="https://www.themealdb.com/api/json/v1/1/random.php";
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, api_url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("rb", "res = " + response.toString());
                                infoActivity.putExtra("jsonResponse", response.toString());

                                try {
                                    String titleMeal = response.getJSONArray("meals").getJSONObject(0).getString("strMeal");
                                    String categMeal = response.getJSONArray("meals").getJSONObject(0).getString("strCategory");
                                    String areaMeal = response.getJSONArray("meals").getJSONObject(0).getString("strArea");
                                    String imgUrl = response.getJSONArray("meals").getJSONObject(0).getString("strMealThumb");
                                    if(!TextUtils.isEmpty(imgUrl)){
                                        ImageView ivMeal = popupView.findViewById(R.id.imgPopContent);
                                        Picasso.get().load(imgUrl).error(R.drawable.btn_blackheart).into(ivMeal);
                                        ivMeal.setVisibility(View.VISIBLE);
                                    }
                                    TextView title = popupView.findViewById(R.id.txtPopTitle);
                                    title.setText(titleMeal);
                                    title.setVisibility(View.VISIBLE);
                                    TextView categ = popupView.findViewById(R.id.txtPopSubTitle);
                                    categ.setText(categMeal);
                                    categ.setVisibility(View.VISIBLE);
                                    TextView area = popupView.findViewById(R.id.txtPopContentInfo);
                                    area.setText(areaMeal);
                                    area.setVisibility(View.VISIBLE);
                                } catch (JSONException e) {
                                    Log.e("rb", "JSON : " +  e.getMessage());
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                title.setText("That didn't work!");
                            }
                        });
                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
                break;
            default: return;
        }
        if(view.getId() == R.id.box_3 || view.getId() == R.id.box_4){
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
        Log.d("rb", "run shuffleBox");
        listBox = new ArrayList<>();
        listBox.add("number");
        listBox.add("meal");
        listBox.add("color");
        listBox.add("movie");

        Collections.shuffle(listBox);

        this.btnN = findViewById(R.id.box_1);
        this.btnN.setText(listBox.get(0));
        this.btnN = findViewById(R.id.box_2);
        this.btnN.setText(listBox.get(1));
        this.btnN = findViewById(R.id.box_3);
        this.btnN.setText(listBox.get(2));
        this.btnN = findViewById(R.id.box_4);
        this.btnN.setText(listBox.get(3));
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