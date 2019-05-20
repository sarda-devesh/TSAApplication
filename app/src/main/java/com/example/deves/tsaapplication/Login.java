package com.example.deves.tsaapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void main() {
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("login","L");
        startActivity(i);
    }

    private void login() {
        EditText first = findViewById(R.id.editText2);
        EditText second = findViewById(R.id.editText3);
        String a = first.getText().toString();
        String b = second.getText().toString();
        String display = "Password doesn't match";
        if(a.equals(b)) {
            display = "Created account";
            first.getText().clear();
            second.getText().clear();
            EditText user = findViewById(R.id.newuser);
            user.getText().clear();
        }
        Toast.makeText(getApplicationContext(),display, Toast.LENGTH_SHORT).show();
    }
}
