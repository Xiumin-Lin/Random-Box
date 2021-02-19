package fr.iut.random_box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
//    private Button btnN, btnC, btnM;
//    private TextView cptN, cptC, cptM;
    private DatabaseReference db;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = FirebaseDatabase.getInstance().getReference();
        this.activity = this;

//        this.btnN = findViewById(R.id.btnNumber);
//        this.cptN = findViewById(R.id.textNum);
//
//        this.btnC = findViewById(R.id.btnColor);
//        this.cptC = findViewById(R.id.textColor);
//
//        this.btnM = findViewById(R.id.btnMovie);
//        this.cptM = findViewById(R.id.textMovie);

//        this.btnN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.child("stats").child("box_number").setValue(ServerValue.increment(1));
//            }
//        });
//
//        this.btnC.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.child("stats").child("box_color").setValue(ServerValue.increment(1));
//
//                Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
//                startActivity(infoActivity);
//                finish();
//            }
//        });

//        this.btnM.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.child("stats").child("box_movie").setValue(ServerValue.increment(1));
//                AlertDialog.Builder popup = new AlertDialog.Builder(activity);
//                popup.setTitle("box_movie Popup");
//                popup.setMessage("Info sur le le film : ...");
//                popup.setPositiveButton("Get More Info", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
//                        startActivity(infoActivity);
//                        activity.finish();
//                    }
//                });
//                popup.show();
//            }
//        });
    }

    public void onClickBtnBox(View view){
        String box_name = "box_";
        switch (view.getId()){
            case R.id.btnNumber:
                box_name += "number";
                break;
            case R.id.btnColor:
                box_name += "color";
                break;
            case R.id.btnMovie:
                box_name += "movie";
                break;
            default:
                return;
        }
        //increment stat of the selected box
        db.child("stats").child(box_name).setValue(ServerValue.increment(1));

        if(view.getId() == R.id.btnNumber){
            AlertDialog.Builder popup = new AlertDialog.Builder(activity);
            popup.setTitle("Number Popup");
            popup.setMessage("" + Box.getRandomNumber(4, 9));
            popup.show();
        }

        if(view.getId() == R.id.btnMovie){
            AlertDialog.Builder popup = new AlertDialog.Builder(activity);
            popup.setTitle("Movie Popup");
            popup.setMessage("Info sur le le film : ...");
            popup.setNeutralButton("Get More Info", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
                    startActivity(infoActivity);
                    activity.finish();
                }
            });
            popup.show();
        }
    }
}