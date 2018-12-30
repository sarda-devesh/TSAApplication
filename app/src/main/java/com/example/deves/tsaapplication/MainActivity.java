package com.example.deves.tsaapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.ImageView;
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
    ArrayList<String> locations;
    Button addper;
    Button done;
    String currentname = "NA";
    Calendar cal;
    int numberofquestions;
    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cal = Calendar.getInstance();
        initialize();
        startCameraSource();
        addper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String a = "student response";
               if(locations.size() == 0) {
                   saveteacheranswers();
                   addper.setText("Add Student Answer");
                   a = "teacher key";
               } else {
                   savestudentanswers();
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
    }

    private void launch() {
        Intent i = new Intent(this,DisplayActivity.class);
        i.putStringArrayListExtra("locations",locations);
        startActivity(i);
    }

    private void initialize() {
        mCameraView = findViewById(R.id.surfaceView);
        mTextView = findViewById(R.id.text_view);
        locations = new ArrayList<String>();
        addper = (Button) findViewById(R.id.addper);
        done = (Button) findViewById(R.id.done);

    }

    private void saveteacheranswers() {
        String[] response = getresponse(0);
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

    private void savestudentanswers() {
        String[] response = getresponse(1);
        String[] ans = read(locations.get(0));
        getscoreandwritetofile(response,ans);
    }

    private void getscoreandwritetofile(String[] a, String[] b) {
        if(a.length != b.length) {
            Log.d(TAG,"Not the same size");
            return;
        }
        int s = 0;
        boolean[] correct = new boolean[a.length];
        for(int i = 0; i < correct.length;i++) {
            if(a[i].equals(b[i])) {
                correct[i] = true;
                s++;
            }
        }
        String filename = (s + "\\" + correct.length) + " " + currentname + " " + cal.getTime().toString();
        String t = "";
        for(int i = 0; i < correct.length;i++) {
            t += a[i] + " " + String.valueOf(correct[i]) + " " + b[i] + "\n";
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

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
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

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
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
