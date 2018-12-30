package com.example.deves.tsaapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import android.widget.LinearLayout;

import com.google.android.gms.vision.text.Line;

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
    }

    private void dis() {
        LinearLayout l = (LinearLayout) findViewById(R.id.display);
        for(int i = 1; i < locations.size();i++) {
            TextView tv =new TextView(this);
            tv.setLayoutParams(lparams);
            tv.setTextSize(30);
            tv.setId(i);
            String[] temp = locations.get(i).split(" ");
            String message = temp[0] + "       " + temp[1];
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

    private void show(int id) {
        Intent launch = new Intent(this,StudentActivity.class);
        launch.putStringArrayListExtra("locations",locations);
        launch.putExtra("ID",id);
        startActivity(launch);
    }

}

