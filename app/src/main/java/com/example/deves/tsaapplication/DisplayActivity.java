package com.example.deves.tsaapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;



public class DisplayActivity extends AppCompatActivity {
    ArrayList<String> locations = null;
    LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        if(getIntent().hasExtra("locations")) {
            locations = getIntent().getStringArrayListExtra("locations");
        }
        lparams.setMargins(0,25,0,25);
        dis();
        Button b = findViewById(R.id.back1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });
    }

    private void goback() {
        Intent i = new Intent(this,MainActivity.class);
        i.putStringArrayListExtra("locations",locations);
        i.putExtra("login","L");
        startActivity(i);
    }

    //Display an overview of the scores of the students
    private void dis() {
        LinearLayout l = findViewById(R.id.display);
        for(int i = 1; i < locations.size();i++) {
            TextView tv =new TextView(this);
            tv.setLayoutParams(lparams);
            tv.setTextSize(30);
            tv.setId(i);
            String[] temp = locations.get(i).split(" ");
            String message = temp[0] + "\\" + temp[1] + "       " + temp[2];
            tv.setText(message);
            tv.setTextColor(Color.GREEN);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show(v.getId());
                }
            });
            l.addView(tv);
        }
    }
    //Launch an intent to show a more detalied view of any student's performance
    private void show(int id) {
        Intent launch = new Intent(this,StudentActivity.class);
        launch.putStringArrayListExtra("locations",locations);
        launch.putExtra("ID",id);
        startActivity(launch);
    }

}

