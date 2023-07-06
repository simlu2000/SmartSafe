package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

import java.util.ArrayList;
import java.util.List;

public class DetectionActivity extends AppCompatActivity {
    private int timeSelected = 0;
    private ListView numeroCellulareList;
    private List<String> itemList;
    private ArrayAdapter<String> adapter;
    private CountDownTimer timeCountDown;
    private int timeProgress = 0;
    private long pauseOffSet = 0;
    private boolean isStart = true;
    private PersonData dbPerson;

    private ImageButton yesBtn, noBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        numeroCellulareList = findViewById(R.id.numeroCellulareList);
        yesBtn = findViewById(R.id.yesBtn);
        noBtn = findViewById(R.id.noBtn);

        dbPerson = new PersonData(DetectionActivity.this);
        PersonInformation user = dbPerson.getPerson();

        ProgressBar progressBar = findViewById(R.id.pbTimer);
        timeSelected = 10;
        progressBar.setMax(timeSelected);
        timePause();
        progressBar.setProgress(10);
        //Toast.makeText(this, timeSelected + " sec all'invio del SOS", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();

        itemList = new ArrayList<>();
        itemList = getNumbers(user.getContattoEmergenza(), user.getTelefoniEmergenza());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        numeroCellulareList.setAdapter(adapter);

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
                Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentHome);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePause();
                Intent intentSos = new Intent(getString(R.string.LAUNCH_SOSACTIVITY)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentSos);
            }
        });

        /*
         * Button startBtn = findViewById(R.id.btnPlayPause);
         * startBtn.setOnClickListener(new View.OnClickListener() {
         *
         * @Override
         * public void onClick(View v) {
         * startTimerSetup();
         * }
         * });
         */

        /*
         * ImageButton resetBtn = findViewById(R.id.ib_reset);
         * resetBtn.setOnClickListener(new View.OnClickListener() {
         *
         * @Override
         * public void onClick(View v) {
         * resetTime();
         * }
         * });
         */

        /*
         * TextView addTimeTv = findViewById(R.id.tv_addTime);
         * addTimeTv.setOnClickListener(new View.OnClickListener() {
         *
         * @Override
         * public void onClick(View v) {
         * addExtraTime();
         * }
         * });
         */
    }

    private List<String> getNumbers(String contattoEmergenza, String telefoniEmergenza) {
        String[] contatti = contattoEmergenza.split(",");
        String[] telefoni = telefoniEmergenza.split(",");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < contatti.length; i++) {
            list.add(contatti[i] + ": " + telefoni[i]);
        }
        return list;
    }

    private void timePause() {
        if (timeCountDown != null) {
            timeCountDown.cancel();
        }
    }

    /*
     * private void startTimerSetup() {
     * Button startBtn = findViewById(R.id.btnPlayPause);
     * if (timeSelected > timeProgress) {
     * if (isStart) {
     * startBtn.setText("Pause");
     * startTimer(pauseOffSet);
     * isStart = false;
     * } else {
     * isStart = true;
     * startBtn.setText("Resume");
     * timePause();
     * }
     * } else {
     * Toast.makeText(this, "Enter Time", Toast.LENGTH_SHORT).show();
     * }
     * }
     */
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
                Intent intentSos = new Intent(getString(R.string.LAUNCH_SOSACTIVITY));
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