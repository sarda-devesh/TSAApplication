package com.example.deves.tsaapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    SurfaceView mCameraView;
    TextView mTextView;
    CameraSource mCameraSource;
    ArrayList<String> locations = null;
    Button addper;
    Button done;
    String currentname = "NA";
    Calendar cal;
    int numberofquestions;
    String a = "";
    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!getIntent().hasExtra("login")) {
            Intent i = new Intent(this,Login.class);
            startActivity(i);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cal = Calendar.getInstance();
        initialize();
        startCameraSource();
        addper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(locations.size() == 0) {
                   String[] response = getresponse(0);
                   saveteacheranswers(response);
                   addper.setText("Add Student Answer");
                   a = "teacher key";
               } else {
                   String[] response = getresponse(1);
                   savestudentanswers(response);
                   a = "student response";
               }
               Toast.makeText(getApplicationContext(),"Recorded " + a, Toast.LENGTH_SHORT).show();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraSource.stop();
                launch();
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manualinput();
            }
        });
        Button previous = findViewById(R.id.prev);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeprevious();
            }
        });
    }

    private void seeprevious() {
        Intent i = new Intent(this, PreviousTests.class);
        if(locations != null) {
            i.putStringArrayListExtra("locations",locations);
        }
        startActivity(i);
    }

    private void manualinput() {
        Intent i = new Intent(this,UserInput.class);
        i.putStringArrayListExtra("locations",locations);
        int code = Math.min(locations.size(),1);
        i.putExtra("code",code);
        i.putExtra("questions",numberofquestions);
        startActivity(i);
    }
    //Launch an intent to the overview of the student performances
    private void launch() {
        Intent i = new Intent(this,DisplayActivity.class);
        i.putStringArrayListExtra("locations",locations);
        startActivity(i);
    }
    //Initialize all the necessary variables for the method
    private void initialize() {
        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);
        addper = findViewById(R.id.addper);
        done = findViewById(R.id.done);
        if(getIntent().hasExtra("locations")) {
            locations = getIntent().getStringArrayListExtra("locations");
            if(locations.size() > 1) {
                addper.setText("Add Student Answer");
            }
        } else {
            locations = new ArrayList<String>();
        }
        if(getIntent().hasExtra("questions")) {
            numberofquestions = getIntent().getIntExtra("questions",0);
        }
        if(getIntent().hasExtra("response")) {
            String[] response = getIntent().getStringArrayExtra("response");
            if(response.length == numberofquestions) {
                saveteacheranswers(response);
                a = "teacher key";
            } else {
                currentname = response[0];
                response = Arrays.copyOfRange(response,1,response.length);
                savestudentanswers(response);
                a = "student response";
            }
            addper.setText("Add Student Answer");
            Toast.makeText(getApplicationContext(),"Recorded " + a, Toast.LENGTH_SHORT).show();
        }
    }
    //Save the response of the teacher in a directory
    private void saveteacheranswers(String[] response) {
        numberofquestions = response.length;
        String t = "";
        for(int i = 0; i < response.length;i++) {
            t += response[i] + "\n";
        }
        String filename = cal.getTime().toString();
        File file = new File(this.getFilesDir(), filename);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(t.getBytes());
            outputStream.close();
            locations.add(filename);
        } catch (Exception e) {
            addper.setText(e.getMessage());
            mCameraSource.stop();
        }
    }
    //Process the student response
    private void savestudentanswers(String[] response) {

        String[] ans = read(locations.get(0));
        getscoreandwritetofile(response,ans);
    }
    //Calculate the student score and write it to the directory 
    private void getscoreandwritetofile(String[] a, String[] b) {
        if(a.length != b.length) {
            Log.d(TAG,"Not the same size");
            return;
        }
        int s = 0;
        boolean[] correct = new boolean[a.length];
        for(int i = 0; i < correct.length;i++) {
            if(a[i].equalsIgnoreCase(b[i])) {
                correct[i] = true;
                s++;
            }
        }
        String filename = (s + " " + correct.length) + " " + currentname + " " + cal.getTime().toString();
        String t = "";
        for(int i = 0; i < correct.length;i++) {
            t += a[i] + " " + correct[i] + " " + b[i] + "\n";
        }
        File file = new File(this.getFilesDir(), filename);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename,Context.MODE_PRIVATE);
            outputStream.write(t.getBytes());
            outputStream.close();
            locations.add(filename);
        } catch (Exception e) {
            addper.setText(e.getMessage());
            mCameraSource.stop();
        }
    }
    //Read in the teacher key when grading student responses 
    private String[] read(String d) {
        String[] answers = null;
        try {
            FileInputStream fis = getApplication().openFileInput(d);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            answers = new String[numberofquestions];
            for(int i = 0; i < answers.length;i++) {
                answers[i] = bufferedReader.readLine();
            }

        } catch (Exception e) {
            addper.setText(e.getMessage());
            mCameraSource.stop();
        }
        return answers;
    }

    //Get the characters detected by the Vision's OCR technology
    private String[] getresponse(int start) {
        String line = mTextView.getText().toString();
        String[] lines = line.split("\n");
        if(start == 0) {
            return lines;
        }
        String[] student = new String[lines.length - 1];
        currentname = lines[0];
        for(int i = 0; i < student.length;i++) {
            student[i] = lines[i + 1];
        }
        return student;
    }
    //Initilaze the input from the camera and the live display
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //Set up the processor to send information from the camera to the OCR technology and receive the result
    private void startCameraSource() {
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

}
