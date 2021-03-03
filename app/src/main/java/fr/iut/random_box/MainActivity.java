package fr.iut.random_box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import org.w3c.dom.Text;

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
        this.btnM = findViewById(R.id.box_4);
        this.btnM.setText(R.string.movie);
    }

    public void onClickBtnBox(View view){
        String box_name = "";

        switch (view.getId()){
            case R.id.box_1: box_name += "number"; break;
            case R.id.box_2: box_name += "color"; break;
            case R.id.box_3: box_name += "meal"; break;
            case R.id.box_4: box_name += "movie"; break;
            default: return;
        }
        //increment stat of the selected box
        db.child("stats").child("box").child(box_name).setValue(ServerValue.increment(1));

        //create & set intent
        Intent infoActivity = new Intent(getApplicationContext(), InfoActivity.class);
        infoActivity.putExtra("box_name", box_name);

        //create box popup builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //activity
        //popup.setTitle(box_name.substring(0, 1).toUpperCase() + box_name.substring(1));   //Capitalize
        //builder.setTitle(box_name.toUpperCase());

        // set the custom layout
        final View popupView = getLayoutInflater().inflate(R.layout.popup,null);
        builder.setView(popupView);
        TextView title = popupView.findViewById(R.id.txtPopupTitle);

        LinearLayout popupLinearLayout = (LinearLayout) popupView.findViewById(R.id.popupLayout);
        Log.d("aaa", popupLinearLayout.toString());
        switch (view.getId()){
            case R.id.box_1:
                TextView txtRandNumber = new TextView(this);
                String randNum = "" + Box.getRandomNumber();
                txtRandNumber.setText(randNum);
                txtRandNumber.setTextAppearance(R.style.rand_number);
                popupLinearLayout.addView(txtRandNumber);
            case R.id.box_2:
            case R.id.box_3:
            case R.id.box_4:
                title.setText(box_name);; break;
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
        AlertDialog popup = builder.create();
        popup.show();
    }
}