package fr.iut.random_box;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

public class InfoActivity extends AppCompatActivity {
    private Button btnBack;
    private TextView boxName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        this.btnBack = findViewById(R.id.btnInfoBack);
        this.boxName = findViewById(R.id.txtInfoBoxName);

        //set data send by activity_main
        Intent intent = getIntent();
        boxName.setText(intent.getStringExtra("box_name"));
        try {
            JSONObject jsonBox = new JSONObject(intent.getStringExtra("jsonResponse"));
            TextView t = findViewById(R.id.txtInfoContent);
            t.setText(jsonBox.toString(4));
            Log.d("rb", "array = " + jsonBox.getJSONArray("meals"));
            Iterator<String> i = jsonBox.getJSONArray("meals").getJSONObject(0).keys();
            while (i.hasNext()){
                String s = i.next();
                Log.d("rb", s + " = " + jsonBox.getJSONArray("meals").getJSONObject(0).get(s));
            }
        } catch (JSONException e) {
            Log.d("rb", "Error while getting json data : " + e.getMessage());
        }

        this.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
    }
}