package com.example.ringlife;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton bttHome, bttSos;
    private Button bttChangeAna, bttChangeMed, bttChangePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bttHome = findViewById(R.id.bttHome);
        bttSos = findViewById(R.id.bttSos);
        bttChangeAna = findViewById(R.id.bttChangeAna);
        bttChangeMed = findViewById(R.id.bttChangeMed);
        bttChangePass = findViewById(R.id.bttChangePass);

        bttSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAlarm();
            }
        });

        bttHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                startActivity(intentHome);
            }
        });
    }

    private void callAlarm() {
        Intent intentDetect = new Intent(getString(R.string.LAUNCH_DETECTIONACTIVITY));
        intentDetect.putExtra("latitude", String.valueOf(latitudeSos));
        intentDetect.putExtra("longitude", String.valueOf(longitudeSos));
        startActivity(intentDetect);
    }
}
