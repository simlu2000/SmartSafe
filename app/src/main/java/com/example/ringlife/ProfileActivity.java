package com.example.ringlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileActivity extends AppCompatActivity {
    private ImageButton bttHome, bttSos, bttManuale, bttPrivacy, bttCrediti;
    private Button bttChangeAna, bttChangeMed, bttChangePin, bttDelete;
    private PersonData dbPerson;
    private PersonInformation user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bttHome = findViewById(R.id.bttHome);
        bttSos = findViewById(R.id.bttSos);
        bttChangeAna = findViewById(R.id.bttChangeAna);
        bttChangeMed = findViewById(R.id.bttChangeMed);
        bttChangePin = findViewById(R.id.bttChangePin);
        bttDelete = findViewById(R.id.bttDelete);
        bttManuale = findViewById(R.id.bttManuale);
        bttPrivacy = findViewById(R.id.bttPrivacy);
        bttCrediti = findViewById(R.id.bttCrediti);

        dbPerson = new PersonData(this);
        user = dbPerson.getPerson();

        bttChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creo l'interfaccia per cambiare PIN
                // Crea un LinearLayout per contenere i tuoi EditText
                LinearLayout layout = new LinearLayout(ProfileActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                // Crea i due EditText che serviranno per l'input
                EditText oldPinEditText = new EditText(ProfileActivity.this);
                oldPinEditText.setHint("Inserisci il vecchio PIN");
                oldPinEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD); // Imposta
                                                                                                                     // l'input
                                                                                                                     // come
                                                                                                                     // numerico
                LinearLayout.LayoutParams oldPinParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                oldPinParams.setMargins(70, 20, 70, 20); // Imposta i margini (left, top, right, bottom)
                oldPinEditText.setLayoutParams(oldPinParams);
                layout.addView(oldPinEditText); // Aggiungi il primo EditText al layout

                EditText newPinEditText = new EditText(ProfileActivity.this);
                newPinEditText.setHint("Inserisci il nuovo PIN");
                newPinEditText.setInputType(InputType.TYPE_CLASS_NUMBER); // Imposta l'input come numerico
                LinearLayout.LayoutParams newPinParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                newPinParams.setMargins(70, 20, 70, 20); // Imposta i margini (left, top, right, bottom)
                newPinEditText.setLayoutParams(newPinParams);
                layout.addView(newPinEditText); // Aggiungi il secondo EditText al layout

                MaterialAlertDialogBuilder dialogPin = new MaterialAlertDialogBuilder(ProfileActivity.this)
                        .setTitle("Cambia PIN")
                        .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("dialogPin", "Annulla");
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("dialogPin", "OK");
                                // Qui puoi ottenere i valori inseriti nei campi di input
                                String oldPin = oldPinEditText.getText().toString();
                                String newPin = newPinEditText.getText().toString();

                                if (oldPin.matches("") || newPin.matches("")) {
                                    Toast.makeText(ProfileActivity.this, "Campo pin vuoto", Toast.LENGTH_LONG).show();
                                } else {
                                    if (!oldPin.matches("[0-9.]+") && !newPin.matches("[0-9.]+")) {
                                        Toast.makeText(ProfileActivity.this, "Campo pin non valido", Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        if (oldPin.equals(user.getPIN())) {
                                            dbPerson.updatePerson(newPin, user.getCodiceFiscale());
                                            updateData();
                                            Toast.makeText(ProfileActivity.this, "Pin modificato correttamente",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "PIN di accesso, errato!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                                dialog.dismiss();
                                /*
                                 * Intent intentHome = new Intent("com.example.ringlife.HomeActivity.java");
                                 * startActivity(intentHome);
                                 */
                            }
                        });

                dialogPin.setView(layout); // Questo imposta il tuo layout personalizzato come vista per il dialog
                dialogPin.show();
            }
        });

        bttDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                dbPerson.deletePerson(user.getCodiceFiscale());

                                Toast.makeText(ProfileActivity.this, "Account eliminato!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });

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

        bttManuale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentManuale = new Intent(getString(R.string.LAUNCH_MANUALEACTIVITY));
                startActivity(intentManuale);
            }
        });

        bttCrediti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCrediti = new Intent(getString(R.string.LAUNCH_CREDITIACTIVITY));
                startActivity(intentCrediti);
            }
        });

        bttPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPrivacy = new Intent(getString(R.string.LAUNCH_PRIVACYACTIVITY));
                startActivity(intentPrivacy);
            }
        });

    }

    public void updateData() {
        user = dbPerson.getPerson();
    }
}
