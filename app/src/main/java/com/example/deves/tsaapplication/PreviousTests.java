package com.example.deves.tsaapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PreviousTests extends AppCompatActivity {
    final int IDSTART = 400;
    final boolean TESTING = true;
    LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    LinearLayout display;
    DatabaseReference databaseTests;
    ArrayList<String> locations = null;
    ArrayList<TestScores> scorestodisplay = new ArrayList<TestScores>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previoustests);
        if(getIntent().hasExtra("locations")) {
            locations = getIntent().getStringArrayListExtra("locations");
        }
        lparams.setMargins(0,35,0,35);
        databaseTests = FirebaseDatabase.getInstance().getReference("Tests");
        display = findViewById(R.id.serverdisplay);
        getandshowscores();
        Button back = findViewById(R.id.serverdataback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });
    }

    private void goback() {
        Intent i = new Intent(this,MainActivity.class);
        if(locations != null) {
            i.putStringArrayListExtra("locations",locations);
        }
        i.putExtra("login","L");
        startActivity(i);
    }

    private void getandshowscores() {
        databaseTests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    for(DataSnapshot testSnapshot: dataSnapshot.getChildren()) {
                        TestScores test = testSnapshot.getValue(TestScores.class);
                        if(TESTING == test.isTesting()) {
                            scorestodisplay.add(test);
                        }
                    }
                    TextView tv = findViewById(R.id.prevtitle);
                    String text = scorestodisplay.size() + " Tests";
                    tv.setText(text);
                    show();
                } catch (Exception e) {
                    errordisplay(e.getMessage());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               errordisplay(databaseError.getMessage());
            }
        });
    }

    private void errordisplay(String display) {
        Toast.makeText(this,display,Toast.LENGTH_LONG).show();
    }

    private void displaytest(int id) {
        id -= IDSTART;
        TestScores ts = scorestodisplay.get(id);
        List<String> scores = ts.testscores;
        ArrayList<String> transfer = new ArrayList<String>();
        for(int i = 1; i < scores.size();i++) {
            transfer.add(scores.get(i));
        }
        String name = ts.testname;
        Intent i = new Intent(this,DisplayScores.class);
        i.putStringArrayListExtra("Scores",transfer);
        i.putExtra("Name",name);
        i.putStringArrayListExtra("locations",locations);
        startActivity(i);
    }

    private void show() {
        for(int i = 0; i < scorestodisplay.size();i++) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(lparams);
            tv.setTextSize(28);
            tv.setId(IDSTART + i);
            tv.setText(scorestodisplay.get(i).testname);
            tv.setTextColor(Color.GREEN);
            display.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displaytest(v.getId());
                }
            });
        }
    }
}
