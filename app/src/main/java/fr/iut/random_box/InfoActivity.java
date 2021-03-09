package fr.iut.random_box;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //set data send by MainActivity
        Intent intent = getIntent();
        try {
            JSONObject jsonBox = new JSONObject(intent.getStringExtra("jsonResponse"));
            String titleMeal = jsonBox.getString("strMeal");
            String longMeal = "Instructions :\n" + jsonBox.getString("strInstructions");
            String imgUrl = jsonBox.getString("strMealThumb");

            //list of items
            Map<String, String> itemMap = new HashMap<>();
            itemMap.put("Area", jsonBox.getString("strArea"));
            itemMap.put("Category", jsonBox.getString("strCategory"));
            //get list view
            ListView infoItems = findViewById(R.id.listInfo);
            infoItems.setAdapter(new BoxInfoItemAdapter(this, itemMap));

            if(!TextUtils.isEmpty(imgUrl)){
                ImageView ivMeal = findViewById(R.id.imgInfoContent);
                Picasso.get().load(imgUrl).error(R.drawable.btn_blackheart).into(ivMeal);
            }
            TextView title = findViewById(R.id.txtInfoContentTitle);
            title.setText(titleMeal);
//            TextView categ = findViewById(R.id.txtInfoContentSubtitle);
//            categ.setText(categMeal);
            TextView longtext = findViewById(R.id.txtInfoContentScroll);
            longtext.setText(longMeal);

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