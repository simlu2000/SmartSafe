package com.example.ringlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvHelloProfile;
    private ImageButton bttHome, bttSos;
    private Button bttChangeAna, bttChangeMed, bttChangePass, bttDelete;
    private PersonData dbPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bttHome = findViewById(R.id.bttHome);
        bttSos = findViewById(R.id.bttSos);
        tvHelloProfile = findViewById(R.id.tvHelloProfile);
        bttChangeAna = findViewById(R.id.bttChangeAna);
        bttChangeMed = findViewById(R.id.bttChangeMed);
        bttChangePass = findViewById(R.id.bttChangePass);
        bttDelete = findViewById(R.id.bttDelete);

        dbPerson = new PersonData(this);
        PersonInformation user = dbPerson.getPerson();

        // Inserisco nome account in alto
        tvHelloProfile.append(" " + user.getNome());

        MaterialAlertDialogBuilder dialogPin = new MaterialAlertDialogBuilder(ProfileActivity.this)
                .setTitle("Cambia PIN")
                .setMessage("Premendo 'OK' cambierai il tuo pin in 00000\n\n")
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogInterface", "Annulla");
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("DialogInterface", "OK");
                        String query = "SET Pin = '00000' WHERE CodiceFiscale = '" + user.getCodiceFiscale() + "'";
                        dbPerson.updatePerson(query);


                        Intent intentMain = new Intent("com.example.ringlife.MainActivity");
                        startActivity(intentMain);
                    }
                });

        bttChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPin.show();
            }
        });

        MaterialAlertDialogBuilder dialogDelete = new MaterialAlertDialogBuilder(ProfileActivity.this)
            .setTitle("Elimina account")
            .setMessage("Premendo 'OK' perderai tutti i dati del tuo account per sempre\n\n")
            .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("DialogInterface", "Annulla");
                }
            })
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("DialogInterface", "OK");
                    /*dbPerson.deletePerson();*/


                    Intent intentMain = new Intent("com.example.ringlife.MainActivity");
                    startActivity(intentMain);
                }
            });

        bttDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDelete.show();
            }
        });



        bttHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                startActivity(intentHome);
            }
        });
        bttSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDetect = new Intent(getString(R.string.LAUNCH_DETECTIONACTIVITY));
                startActivity(intentDetect);
            }
        });

    }
}
