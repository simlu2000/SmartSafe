package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetectionActivity extends AppCompatActivity {
    private int timeSelected = 0;
    private CountDownTimer timeCountDown;
    private int timeProgress = 0;
    private long pauseOffSet = 0;
    private boolean isStart = true;
    private String latitude, longitude;


    private ImageButton yesBtn, noBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);

        ProgressBar progressBar = findViewById(R.id.pbTimer);
        timeSelected = 10;
        progressBar.setMax(timeSelected);
        timePause();
        progressBar.setProgress(10);
        Toast.makeText(this, timeSelected + " sec all'invio del SOS", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTimer(pauseOffSet);
            }
        }, 1000);



        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePause();
                Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentHome);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePause();
                Intent intentSos = new Intent(getString(R.string.LAUNCH_SOSACTIVITY)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentSos.putExtra("latitude", String.valueOf(latitude));
                intentSos.putExtra("longitude", String.valueOf(longitude));
                startActivity(intentSos);
            }
        });

        /*Button startBtn = findViewById(R.id.btnPlayPause);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimerSetup();
            }
        });*/

        /*ImageButton resetBtn = findViewById(R.id.ib_reset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTime();
            }
        });*/

        /*TextView addTimeTv = findViewById(R.id.tv_addTime);
        addTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExtraTime();
            }
        });*/
    }

    private void timePause() {
        if (timeCountDown != null) {
            timeCountDown.cancel();
        }
    }

    /*private void startTimerSetup() {
        Button startBtn = findViewById(R.id.btnPlayPause);
        if (timeSelected > timeProgress) {
            if (isStart) {
                startBtn.setText("Pause");
                startTimer(pauseOffSet);
                isStart = false;
            } else {
                isStart = true;
                startBtn.setText("Resume");
                timePause();
            }
        } else {
            Toast.makeText(this, "Enter Time", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void startTimer(long pauseOffSetL) {
        ProgressBar progressBar = findViewById(R.id.pbTimer);
        timeCountDown = new CountDownTimer((timeSelected * 1000) - (pauseOffSetL * 1000), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeProgress++;
                pauseOffSet = timeSelected - (millisUntilFinished / 1000);
                progressBar.setProgress(timeSelected - timeProgress);
                TextView timeLeftTv = findViewById(R.id.tvTimeLeft);
                timeLeftTv.setText(String.valueOf(timeSelected - timeProgress));
            }

            @Override
            public void onFinish() {
                // CHIAMA SOS QUIII
                Toast.makeText(DetectionActivity.this, "Avvio SOS in corso...", Toast.LENGTH_SHORT).show();
                Intent intentHome = getIntent();
                latitude = intentHome.getStringExtra("latitude");
                longitude = intentHome.getStringExtra("longitude");
                Intent intentSos = new Intent(getString(R.string.LAUNCH_SOSACTIVITY));
                intentSos.putExtra("latitude", String.valueOf(latitude));
                intentSos.putExtra("longitude", String.valueOf(longitude));
                startActivity(intentSos);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeCountDown != null) {
            timeCountDown.cancel();
            timeProgress = 10;
        }
    }

}
