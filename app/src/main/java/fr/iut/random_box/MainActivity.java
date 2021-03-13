package fr.iut.random_box;

import androidx.annotation.NonNull;
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
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final int NB_MAX_BOX = 6;
    private final DatabaseReference DB_STATS = FirebaseDatabase.getInstance().getReference("stats/box");
    private final ArrayList<String> NAME_BOX_LIST = new ArrayList<>(Arrays.asList("number","color","meal","anime","manga","astronomy")); //TODO make Enum
    private final ArrayList<String> STANDALONE_BOX_LIST = new ArrayList<>(Arrays.asList("number","color"));
    private final ArrayList<Integer> BOX_ID_LIST = new ArrayList<>(Arrays.asList(R.id.box_1,R.id.box_2,R.id.box_3,R.id.box_4,R.id.box_5,R.id.box_6));

    private MainActivity mainActivity;
    private MediaPlayer rollDiceSound; // Dice sound effect
    private MediaPlayer waterDropSound; // Water drop sound effect
    private ArrayList<Integer> colorList;

    private SensorManager mSensorManager; // object to access the device's sensors
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private static final HashMap<String, String> statsList = new HashMap<>();

    /**
     * Initiates the values when the app is started
     * @param savedInstanceState : the instance saved at the moment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mainActivity = this;

        this.rollDiceSound = MediaPlayer.create(this, R.raw.roll_dice);
        this.waterDropSound = MediaPlayer.create(this, R.raw.water_drop);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        // Collect the array in the colors.xml as an array of int and put it into a ArrayList to shuffle
        int[] colorPalette = getResources().getIntArray(R.array.palette);
        this.colorList = new ArrayList<>();
        for(int color : colorPalette) {
            colorList.add(color);
        }
        //display box randomly, has to be run at the end of onCreate
        shuffleBox();
        updateBoxStats();
    }

    /**
     * retrieves the statistics from the database
     */
    private void updateBoxStats() {
        DB_STATS.addValueEventListener(new ValueEventListener() {
            /**
             * Keeps the Database up to date
             * @param snapshot : the snapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("rb", "Update box statistics");
                statsList.clear();
                for(DataSnapshot child: snapshot.getChildren()){
                    statsList.put(child.getKey(), child.getValue().toString());
                }
            }

            /**
             * Prints errors in the log
             * @param error : the error
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("rb", "cancel update stats : " + error.getMessage());
            }
        });
    }

    /**
     * Shows in a popup the content of the box
     * @param view : the view that called the method
     */
    public void onClickBtnBox(View view){
        Button btn = (Button) view;
        waterDropSound.start();
        String box_name = btn.getText().toString();
        Log.d("rb", "Open " + box_name + " box");
        if(!NAME_BOX_LIST.contains(box_name)){
            Log.d("rb", "Unknown box name");
            return;
        }

        //increment stat of the selected box
        DB_STATS.child(box_name).setValue(ServerValue.increment(1));

        //create & set intent
        Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
        infoActivity.putExtra("boxName", box_name);

        //create box popup builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //main activity

        // set the custom layout
        final View popupView = getLayoutInflater().inflate(R.layout.popup,null);
        builder.setView(popupView);

        //update custom layout components
        TextView title = popupView.findViewById(R.id.txtPopTitle);
        title.setText(box_name);

        RandomBox randomBox = RandomBoxFactory.buildBox(box_name);
        if(randomBox == null){
            Log.d("rb", "RandomBox IS NULL");
            return;
        }

        if(!randomBox.needAPI()){
            randomBox.setPopupView(popupView);
        }
        else {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            // Add the request to the RequestQueue.
            if(randomBox.hasJsonObject()){
                queue.add(randomBox.makeJsonObjectRequest(popupView, infoActivity));
            } else {
                queue.add(randomBox.makeJsonArrayRequest(popupView, infoActivity));
            }
        }

        if(!STANDALONE_BOX_LIST.contains(box_name)){
            builder.setNeutralButton("Get More Info", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(infoActivity);
                    mainActivity.finish();
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
     * Creates the popup with statistics when the button stats is clicked on
     * @param view : the view that called the method
     */
    public void onClickStats(View view) {
        Log.d("rb", "Show stats in a ListView" + statsList.toString());
        //create box stats popup builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        //set the custom layout
        final View statsPopup = getLayoutInflater().inflate(R.layout.popup,null);
        builder.setView(statsPopup);
        TextView title = statsPopup.findViewById(R.id.txtPopTitle);
        title.setText(R.string.stats);
        //set stats ListView
        ListView statsListView = statsPopup.findViewById(R.id.listViewStats);
        statsListView.setAdapter(new BoxInfoItemAdapter(this, statsList));
        statsListView.setVisibility(View.VISIBLE);
        //display popup
        AlertDialog popup = builder.create();
        popup.show();
    }

    /**
     * Shuffles the boxes position
     */
    public void shuffleBox(){
        //Makes a rolling dice sound
        Log.d("rb", "Rolling Dice");
        rollDiceSound.start();

        Log.d("rb", "Shuffle All Box");
        Collections.shuffle(NAME_BOX_LIST);
        Collections.shuffle(colorList);

        Button btnBox;
        for(int i = 0; i < NB_MAX_BOX; i++){
            btnBox = findViewById(BOX_ID_LIST.get(i));
            btnBox.setText(NAME_BOX_LIST.get(i));
            btnBox.setBackgroundTintList(ColorStateList.valueOf(colorList.get(i)));
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
            mAccelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
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
