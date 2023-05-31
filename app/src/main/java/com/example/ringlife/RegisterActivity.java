package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita, etTelefono, etPatologie, etAllergie, etContattoEm, etPin;
    private Spinner spSesso, spGruppoSanguigno;
    private Button bttConferma;
    private PersonData dbPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNome = findViewById(R.id.etNome);
        etCognome = findViewById(R.id.etCognome);
        etCodiceFiscale = findViewById(R.id.etCodiceFiscale);
        etDataNascita = findViewById(R.id.etDataNascita);
        etTelefono = findViewById(R.id.etTelefono);
        etPatologie = findViewById(R.id.etPatologie);
        etAllergie = findViewById(R.id.etAllergie);
        etContattoEm = findViewById(R.id.etContattoEm);
        etPin = findViewById(R.id.etPin);

        spSesso = findViewById(R.id.spSesso);
        spGruppoSanguigno = findViewById(R.id.spGruppoSanguigno);

        bttConferma = findViewById(R.id.bttConferma);

        Log.i(TAG, "Prima della funzione");
        insertInDB();
        Log.i(TAG, "Dopo la funzione");

    }

    private void insertInDB(){
        Log.i(TAG, "Entro nella funzione");
        bttConferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = etNome.getText().toString().trim();
                String cognome = etCognome.getText().toString().trim();
                String codiceFiscale = etCodiceFiscale.getText().toString().trim();
                String dataNascita = etDataNascita.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String patologie = etPatologie.getText().toString();
                String allergie = etAllergie.getText().toString();
                String contattoEm = etContattoEm.getText().toString();
                String pin = etPin.getText().toString().trim();

                String sesso = spSesso.getSelectedItem().toString();
                String gruppoSanguigno = spGruppoSanguigno.getSelectedItem().toString();

                if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || dataNascita.isEmpty() ||
                        telefono.isEmpty() || patologie.isEmpty() || allergie.isEmpty() || contattoEm.isEmpty()
                        || pin.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Tutti i campi devono essere compilati", Toast.LENGTH_LONG).show();
                } else {
                    if(pin.length() < 5 && pin.matches("[0-9.]+"))
                        Toast.makeText(RegisterActivity.this, "Inserisci un PIN con almeno 5 cifre e/o deve contenere solo numeri", Toast.LENGTH_LONG).show();
                    else{
                        dbPerson = new PersonData(RegisterActivity.this);
                        PersonInformation personInformation = new PersonInformation(codiceFiscale, nome, cognome,dataNascita, telefono, sesso, gruppoSanguigno, patologie, allergie, contattoEm, pin);
                        dbPerson.addPerson(personInformation);

                        Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                        startActivity(intentHome);
                    }
                }
            }
        });
    }
}
