package fr.iut.random_box;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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