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
    private Button btnN, btnC, btnM;
//    private TextView cptM;
    private DatabaseReference db;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = FirebaseDatabase.getInstance().getReference();
        this.activity = this;

        this.btnN = findViewById(R.id.box_1);
        this.btnN.setText(R.string.number);
        this.btnC = findViewById(R.id.box_2);
        this.btnC.setText(R.string.color);
        this.btnM = findViewById(R.id.box_3);
        this.btnM.setText(R.string.meal);
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
        String box_name = "";
        switch (view.getId()){
            case R.id.box_1:
                box_name += "number";
                break;
            case R.id.box_2:
                box_name += "color";
                break;
            case R.id.box_3:
                box_name += "meal";
                break;
            default:
                return;
        }
        //increment stat of the selected box
        db.child("stats").child("box").child(box_name).setValue(ServerValue.increment(1));

        if(view.getId() == R.id.box_1){
            AlertDialog.Builder popup = new AlertDialog.Builder(activity);
            popup.setTitle(box_name.toUpperCase());
            popup.setMessage("" + Box.getRandomNumber());
            popup.show();
        }

        if(view.getId() == R.id.box_2){
            AlertDialog.Builder popup = new AlertDialog.Builder(activity);
//            popup.setTitle(box_name.substring(0, 1).toUpperCase() + box_name.substring(1));   //Capitalize
            popup.setTitle(box_name.toUpperCase());
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