package com.example.ringlife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Dati utili per accesso e registrazione
    private Button bttInUp;
    private PersonData dbPerson;
    private boolean exist;
    private EditText etPin;

    // Array permessi
    private String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    public static List<String> missingPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final boolean[] isActivityStarted = {false};

        bttInUp = findViewById(R.id.bttInUp);
        etPin = findViewById(R.id.etPin);
        etPin.setVisibility(View.INVISIBLE);

        missingPermissions = new ArrayList<>();

        // Verifica e aggiungi i permessi mancanti alla lista
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        // Verifica se ci sono permessi mancanti
        if (!MainActivity.missingPermissions.isEmpty()) {
            // Array dei permessi mancanti da richiedere
            String[] permissionsToRequest = MainActivity.missingPermissions.toArray(new String[0]);

            // Richiedi i permessi mancanti all'utente
            ActivityCompat.requestPermissions(this, permissionsToRequest, 1);
        }

        dbPerson = new PersonData(this);
        exist = dbPerson.ifExistPerson();

        if(exist){
            etPin.setVisibility(View.VISIBLE);
            bttInUp.setVisibility(View.INVISIBLE);
            PersonInformation user = dbPerson.getPerson();

            //Azioni invio PIN
            etPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL){
                        String getPin = etPin.getText().toString();
                        if (getPin.matches("")) {
                            Toast.makeText(MainActivity.this, "Campo pin vuoto", Toast.LENGTH_LONG).show();
                        }else {
                            if (!getPin.matches("[0-9.]+")) {
                                etPin.setText("");
                                Toast.makeText(MainActivity.this, "Campo pin non valido", Toast.LENGTH_LONG).show();
                            } else {
                                if (user.getPIN().equals(getPin)){
                                    if(!isActivityStarted[0]){
                                        //Toast.makeText(MainActivity.this, "Pin corretto", Toast.LENGTH_LONG).show();
                                        Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                                        startActivity(intentHome);
                                        isActivityStarted[0] = true;
                                        finish();
                                    }
                                }
                                else {
                                    etPin.setText("");
                                    Toast.makeText(MainActivity.this, "Pin errato", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                    return false;
                }
            });

            // Azioni durante la scrittura del PIN
            etPin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String password = s.toString();
                    // Controllo sulla password
                    if (password.equals(user.getPIN()) && !isActivityStarted[0]) {
                        // La password è corretta, avvia un'altra activity
                        Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                        startActivity(intentHome);
                        isActivityStarted[0] = true;
                        finish();
                    }
                }
            });
        }else{
            clickButton();
        }

    }

    //Funzione dei click
    public void clickButton(){
        bttInUp.setOnClickListener((v)->{
            Intent intentReg = new Intent(getString(R.string.LAUNCH_REGISTERACTIVITY));
            startActivity(intentReg);
        });
    }

    // Controllo permessi utente
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                // Almeno un permesso è stato negato, gestisci questa situazione adeguatamente
                Toast.makeText(MainActivity.this, "Chiusura forzata per mancati permessi", Toast.LENGTH_SHORT).show();
                // Chiude l'app
                finish();
            }
        }
    }

}