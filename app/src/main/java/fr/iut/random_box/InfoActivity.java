package fr.iut.random_box;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoActivity extends AppCompatActivity {
    private Button btnBack;
    private TextView boxName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        this.btnBack = findViewById(R.id.btnInfoBack);
        this.boxName = findViewById(R.id.txtInfoContentTitle);

        //set data send by activity_main
        Intent intent = getIntent();
        boxName.setText(intent.getStringExtra("box_name"));
        try {
            JSONObject jsonBox = new JSONObject(intent.getStringExtra("jsonResponse"));

//            ArrayList<String> fields = new ArrayList<>(Arrays.asList("strMeal","strCategory","strArea","strMealThumb"));
//            for(String str: fields){
//
//            }

            String titleMeal = jsonBox.getString("strMeal");
            String categMeal = jsonBox.getString("strCategory");
            String areaMeal = "Area : " + jsonBox.getString("strArea");
            String longMeal = "Instructions :\n" + jsonBox.getString("strInstructions");
            String imgUrl = jsonBox.getString("strMealThumb");
            TextView t = findViewById(R.id.txtInfoContentScroll);
            t.setText(jsonBox.toString(4));

            if(!TextUtils.isEmpty(imgUrl)){
                ImageView ivMeal = findViewById(R.id.imgInfoContent);
                Picasso.get().load(imgUrl).error(R.drawable.btn_blackheart).into(ivMeal);
            }
            TextView title = findViewById(R.id.txtInfoContentTitle);
            title.setText(titleMeal);
            TextView categ = findViewById(R.id.txtInfoContentSubtitle);
            categ.setText(categMeal);
            TextView area = findViewById(R.id.txtInfoField1);
            area.setText(areaMeal);
            TextView longtext = findViewById(R.id.txtInfoContentScroll);
            longtext.setText(longMeal);

//            Iterator<String> i = jsonBox.keys();
//            while (i.hasNext()){
//                String s = i.next();
//                Log.d("rb", s + " = " + jsonBox.get(s));
//            }
        } catch (JSONException e) {
            Log.d("rb", "Error while getting json data : " + e.getMessage());
        }
    }

    public void onClickBack(View view){
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
}