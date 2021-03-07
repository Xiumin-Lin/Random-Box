package fr.iut.random_box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        shuffleBox(null);

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
                txtCTitle.setText("HEX = " + String.format("#%06X", (0xFFFFFF & randColor))); //convert int color to hex format
                txtCTitle.setVisibility(View.VISIBLE); break;
            case "meal":
                ImageView imgContent = popupView.findViewById(R.id.imgPopContent);
                String url = "https://cdn.myanimelist.net/images/anime/13/50521.jpg";
                LoadImage loadImage = new LoadImage(imgContent);
                loadImage.execute(url);
                imgContent.setVisibility(View.VISIBLE);

                TextView imgTitle = popupView.findViewById(R.id.txtPopTitle);
                imgTitle.setText("Hyouka");
                imgTitle.setVisibility(View.VISIBLE);

                TextView subTitle = popupView.findViewById(R.id.txtPopSubTitle);
                subTitle.setText("氷菓");
                subTitle.setVisibility(View.VISIBLE);

                View scrollView = popupView.findViewById(R.id.scrollPopDetail);
                scrollView.setVisibility(View.VISIBLE);
            case "movie": break;
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
     * Shuffles the boxes position
     * @param v : the view that called the method
     */
    public void shuffleBox(View v){
        Log.d("rb", "run shuffleBox");
        listBox = new ArrayList<String>();
        listBox.add("number");
        listBox.add("meal");
        listBox.add("color");
        listBox.add("movie");
        Collections.shuffle(listBox);

        int[] colorPalette = getResources().getIntArray(R.array.palette);
        List<Integer> colorList = new ArrayList<Integer>(colorPalette.length);
        for(int i : colorPalette) {
            colorList.add(i);
        }
        Collections.shuffle(colorList);

        this.btnN = findViewById(R.id.box_1);
        this.btnN.setText(listBox.get(0));
        this.btnN.setBackgroundTintList(ColorStateList.valueOf(colorList.get(0)));
        this.btnN = findViewById(R.id.box_2);
        this.btnN.setText(listBox.get(1));
        this.btnN.setBackgroundTintList(ColorStateList.valueOf(colorList.get(1)));
        this.btnN = findViewById(R.id.box_3);
        this.btnN.setText(listBox.get(2));
        this.btnN.setBackgroundTintList(ColorStateList.valueOf(colorList.get(2)));
        this.btnN = findViewById(R.id.box_4);
        this.btnN.setText(listBox.get(3));
        this.btnN.setBackgroundTintList(ColorStateList.valueOf(colorList.get(3)));

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        /**
         * Detects the shake motion and call the shufflebox function
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

            if (mAccel > 12) {
                Log.d("rb", "device shaken");
                shuffleBox(null);
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