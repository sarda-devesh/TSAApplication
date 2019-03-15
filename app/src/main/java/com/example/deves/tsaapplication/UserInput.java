package com.example.deves.tsaapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;

public class UserInput extends AppCompatActivity {
    LayoutParams lparams = new LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LinearLayout parent;
    int numberofquestions = 0;
    ArrayList<String> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_input);
        lparams.setMargins(0,25,0,25);
        parent = (LinearLayout) findViewById(R.id.manualparent);
        final int code = getIntent().getIntExtra("code",0);
        numberofquestions = getIntent().getIntExtra("questions",0);
        locations = getIntent().getStringArrayListExtra("locations");
        if(code == 0) {
            teacherinput();
        } else {
            studentinput();
        }
        Button done = (Button) findViewById(R.id.mandone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] response;
                if(code == 0) {
                   response = readuserinput(numberofquestions);
                } else {
                    response = readuserinput(numberofquestions + 1);
                    EditText number = (EditText) findViewById(R.id.number);
                    response[0] = number.getText().toString();
                }
                goback(response);
            }
        });
    }

    //Return to the main display page
    private void goback(String[] response) {
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("questions",numberofquestions);
        i.putExtra("response",response);
        i.putStringArrayListExtra("locations",locations);
        startActivity(i);
    }

    //Set the view for taking in teacher's key
    private void teacherinput() {
        Button b = (Button) findViewById(R.id.set);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText n = (EditText) findViewById(R.id.number);
                numberofquestions = Integer.parseInt(n.getText().toString());
                addedit();
            }
        });
    }

    //Set the view to take in student's answers
    private void studentinput() {
        EditText number = (EditText) findViewById(R.id.number);
        number.setLayoutParams(lparams);
        number.setHint("Name of student: ");
        Button b = (Button) findViewById(R.id.set);
        b.setVisibility(View.INVISIBLE);
        numberofquestions = getIntent().getIntExtra("questions",0);
        addedit();
    }

    //Add EditText views where the user can type
    private void addedit() {
        for(int i = 0; i < numberofquestions;i++) {
            EditText e = new EditText(this);
            e.setId(i);
            e.setLayoutParams(lparams);
            e.setHint("Value: ");
            parent.addView(e);
        }
    }

    private String[] readuserinput(int size) {
        String[] t = new String[size];
        int difference = size - numberofquestions;
        for (int i = difference; i < size;i++) {
            EditText e = (EditText) findViewById(i - difference);
            t[i] = e.getText().toString();
        }
        return t;
    }

}
