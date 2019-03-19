package com.example.deves.tsaapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class StudentActivity extends AppCompatActivity {
    //Initialize variables and setup the display 
    ArrayList<String> locations;
    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    int c = 100;
    int correct = 0;
    int total = 0;
    String name = "";
    Calendar cal;
    String[][] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        cal = Calendar.getInstance();
        lparams.setMargins(0,c,0,c);
        lparam.setMargins(c,0,c,0);
        locations = getIntent().getStringArrayListExtra("locations");
        final int index = getIntent().getIntExtra("ID",0);
        read(index);
        Button b = findViewById(R.id.button2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(index);
                goback();
            }
        });
    }

    private void goback() {
        Intent i = new Intent(this,DisplayActivity.class);
        i.putStringArrayListExtra("locations",locations);
        startActivity(i);
    }

    private void update(int index) {
        locations.remove(index);
        String t = "";
        for(int i = 0; i < values.length;i++) {
            String right = values[i][1];
            CheckBox c = findViewById(200 + i);
            if(c.isChecked()) {
                if(right.equals("false")) {
                    right = "true";
                    correct++;
                } else {
                    right = "false";
                    correct--;
                }
            }
            t += values[i][0] + " " + right + " " + values[i][2] + "\n";
        }
        String filename = (correct + " " + total) + " " + name + " " + cal.getTime().toString();
        File file = new File(this.getFilesDir(), filename);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(t.getBytes());
            outputStream.close();
            locations.add(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Read the file from the directory about the student response and display the student answer in red or blue along with the correct answer
    private void read(int index) {
        LinearLayout l = findViewById(R.id.per);
        TextView title = findViewById(R.id.Title);
        String filename = locations.get(index);
        String[] temp = filename.split(" ");
        title.setText(temp[0] + "\\" + temp[1] + "   " + temp[2]);
        correct = Integer.parseInt(temp[0]);
        total = Integer.parseInt(temp[1]);
        name = temp[2];
        values = new String[total][3];
        int count = 0;
        try {
            FileInputStream fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while((line = bufferedReader.readLine()) != null) {
                LinearLayout hor = new LinearLayout(this);
                hor.setLayoutParams(lparams);
                hor.setOrientation(LinearLayout.HORIZONTAL);
                String[] divide = line.split(" ");
                TextView student = createtext(divide[0]);
                values[count][0] = divide[0];
                values[count][1] = divide[1];
                values[count][2] = divide[2];
                if(divide[1].equals("true")) {
                    student.setTextColor(Color.GREEN);
                } else {
                    student.setTextColor(Color.RED);
                }
                hor.addView(student);
                student = createtext(divide[2]);
                hor.addView(student);
                CheckBox cb = new CheckBox(this);
                cb.setId(200 + count);
                hor.addView(cb);
                l.addView(hor);
                count++;
            }
        }  catch (Exception e) {
            title.setText("Error: " + e.getMessage());
        }
    }
    //Add the specified textview to the display
    private TextView createtext(String st) {
        TextView student = new TextView(this);
        student.setLayoutParams(lparam);
        student.setText(st);
        student.setTextSize(24);
        student.setGravity(1);
        return  student;
    }
}