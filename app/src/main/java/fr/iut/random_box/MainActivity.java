package fr.iut.random_box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
        this.btnM = findViewById(R.id.box_4);
        this.btnM.setText(R.string.movie);
    }

    /**
     * Affiche dans une popup le contenu aléatoire d'une boîte lorsque l'user le click
     * @param view : la view qui a appelé la methode
     */
    public void onClickBtnBox(View view){
        String box_name = "";

        switch (view.getId()){
            case R.id.box_1: box_name += "number"; break;
            case R.id.box_2: box_name += "color"; break;
            case R.id.box_3: box_name += "meal"; break;
            case R.id.box_4: box_name += "movie"; break;
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
        switch (view.getId()){
            case R.id.box_1:
                TextView txtRandNumber = popupView.findViewById(R.id.txtPopBigNumber);
                String randNum = "" + RandomBox.getRandomNumber();
                txtRandNumber.setText(randNum);
                txtRandNumber.setVisibility(View.VISIBLE); break;
            case R.id.box_2:
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
                txtCTitle.setText("HEX = " + String.format("#%06X", (0xFFFFFF & randColor))); //covert int color to hex format
                txtCTitle.setVisibility(View.VISIBLE); break;
            case R.id.box_3:
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
            case R.id.box_4: break;
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

}