package com.example.deves.tsaapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class DisplayActivity extends AppCompatActivity {
    ArrayList<String> locations = null;
    LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    DatabaseReference databaseTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        if(getIntent().hasExtra("locations")) {
            locations = getIntent().getStringArrayListExtra("locations");
        }
        databaseTests = FirebaseDatabase.getInstance().getReference("Tests");
        lparams.setMargins(0,25,0,25);
        dis();
        Button b = findViewById(R.id.back1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });
        Button ser = findViewById(R.id.AddtoDatabase);
        ser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writetoserver();
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

    private void writetoserver() {
        EditText editname = findViewById(R.id.testname);
        String name = editname.getText().toString().trim();
        try {
            String id = databaseTests.push().getKey();
            TestScores test = new TestScores(name,locations,id);
            databaseTests.child(id).setValue(test);
            Toast.makeText(this,"Added test to database",Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

}

