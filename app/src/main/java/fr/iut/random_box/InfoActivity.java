package fr.iut.random_box;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class InfoActivity extends AppCompatActivity {
    private InfoActivity activity;
    private RandomBox randomBox;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        this.activity = this;
        //set data send by MainActivity
        this.intent = getIntent();
        String boxName = intent.getStringExtra("boxName");
        this.randomBox = RandomBoxFactory.buildBox(boxName);

        DatabaseReference dbBox = FirebaseDatabase.getInstance().getReference("box/"+boxName);
        if(randomBox == null){
            Log.d("rb", "RandomBox is NULL");
            finish();
        }

        dbBox.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){ //TODO can be improved
                    HashMap<String,String> generalFields = new HashMap<>(); //generic fields of a box
                    HashMap<String,String> additionalFields = new HashMap<>(); //optional info of a box
                    for(DataSnapshot child: snapshot.getChildren()){
                        if(Objects.equals(child.getKey(), "info")){
                            //Get all meta data about additionalFields of the box from database
                            for(DataSnapshot infoChild : child.getChildren()){
                                additionalFields.put(infoChild.getKey(), infoChild.getValue().toString());
                            }
                        }
                        else //Get all meta data about generalFields of the box from database
                            generalFields.put(child.getKey(), child.getValue().toString());
                    }
                    if(!generalFields.isEmpty() && randomBox != null) {
                        try {
                            JSONObject jsonBox = new JSONObject(intent.getStringExtra("jsonResponse"));
                            Log.d("rb", "JSON Response retrieve OK");
                            Log.d("rb", "generalFields = " + generalFields.toString());
                            Log.d("rb", "additionalFields = " + additionalFields.toString());
                            randomBox.setInfoView(activity,jsonBox,generalFields,additionalFields);
                        } catch (JSONException e) {
                            Log.d("rb", "Error while getting/using json data in InfoActivity : " + e.getMessage());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("rb", "Cancelled");
            }
        });
    }

    public void onClickBack(View view){
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}