package com.example.deves.tsaapplication;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    ArrayList<String> locations;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    int c = 150;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        lparams.setMargins(0,c,0,c);
        lparam.setMargins(c,0,c,0);
        locations = getIntent().getStringArrayListExtra("locations");
        int index = getIntent().getIntExtra("ID",0);
        read(index);
    }

    private void read(int index) {
        LinearLayout l = (LinearLayout) findViewById(R.id.per);
        TextView title = (TextView) findViewById(R.id.Title);
        String name = locations.get(index);
        String[] temp = name.split(" ");
        title.setText((temp[0] + "    " + temp[1]));
        try {
            FileInputStream fis = openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                LinearLayout hor = new LinearLayout(this);
                hor.setLayoutParams(lparams);
                hor.setOrientation(LinearLayout.HORIZONTAL);
                String[] divide = line.split(" ");
                TextView student = createtext(divide[0]);
                if(divide[1].equals("true")) {
                    student.setTextColor(Color.GREEN);
                } else {
                    student.setTextColor(Color.RED);
                }
                hor.addView(student);
                student = createtext(divide[2]);
                hor.addView(student);
                l.addView(hor);
            }
        }  catch (Exception e) {
            title.setText("Error: " + e.getMessage());
        }
    }

    private TextView createtext(String st) {
        TextView student = new TextView(this);
        student.setLayoutParams(lparam);
        student.setText(st);
        student.setTextSize(24);
        student.setGravity(1);
        return  student;
    }
}
