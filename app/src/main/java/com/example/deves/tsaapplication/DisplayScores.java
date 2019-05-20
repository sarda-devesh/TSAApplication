package com.example.deves.tsaapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

public class DisplayScores extends AppCompatActivity {
    ArrayList<String> scores = null;
    LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    ArrayList<String> locations = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String name = "";
        setContentView(R.layout.activity_display_scores);
        lparams.setMargins(0,25,0,25);
        if(getIntent().hasExtra("Scores")) {
            scores = getIntent().getStringArrayListExtra("Scores");
        }
        if(getIntent().hasExtra("Name")) {
            name = getIntent().getStringExtra("Name");
        }
        if(getIntent().hasExtra("locations")) {
            locations = getIntent().getStringArrayListExtra("locations");
        }
        TextView tv = findViewById(R.id.scoretitle);
        tv.setText(name);
        show();
        Button button = findViewById(R.id.scoredataback);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });
    }

    private void show() {
        if(scores == null) {
            errordisplay("Couldn't get scores");
            return;
        }
        LinearLayout dis = findViewById(R.id.scoredisplay);
        for(String score: scores) {
            String[] temp = score.split(" ");
            String message = temp[0] + "\\" + temp[1] + "       " + temp[2];
            TextView tv =new TextView(this);
            tv.setLayoutParams(lparams);
            tv.setTextSize(30);
            tv.setText(message);
            tv.setTextColor(Color.GREEN);
            dis.addView(tv);
        }
    }

    private void errordisplay(String display) {
        Toast.makeText(this,display,Toast.LENGTH_LONG).show();
    }

    private void goback() {
        Intent i = new Intent(this,PreviousTests.class);
        if(locations != null) {
            i.putStringArrayListExtra("locations",locations);
        }
        startActivity(i);
    }
}
