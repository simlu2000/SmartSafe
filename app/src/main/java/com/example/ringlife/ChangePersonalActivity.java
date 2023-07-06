package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class ChangePersonalActivity extends AppCompatActivity {

    /* Dichiarazione varibili ChangePersonalActivity Layout */
    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita, etTelefono;
    private Spinner spSesso;
    private ArrayAdapter<CharSequence> adapter;
    private Button bttSalva, bttAnnulla;
    private ImageButton bttHome, bttSos, bttProfile;
    private PersonData dbPerson;
    private PersonInformation user;
    private boolean isActivityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_personal);

        etNome = findViewById(R.id.etNome);
        etCognome = findViewById(R.id.etCognome);
        etCodiceFiscale = findViewById(R.id.etCodiceFiscale);
        etDataNascita = findViewById(R.id.etDataNascita);
        etTelefono = findViewById(R.id.etTelefono);
        spSesso = findViewById(R.id.spSesso);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.Sesso, android.R.layout.simple_spinner_item);
        bttSalva = findViewById(R.id.bttSavePersonal);
        bttAnnulla = findViewById(R.id.bttBackPersonal);
        bttHome = findViewById(R.id.bttHome);
        bttSos = findViewById(R.id.bttSos);
        bttProfile = findViewById(R.id.bttProfile);

        dbPerson = new PersonData(this);
        user = dbPerson.getPerson();

        etNome.setText(user.getNome());
        etCognome.setText(user.getCognome());
        etCodiceFiscale.setText(user.getCodiceFiscale());
        etDataNascita.setText(user.getDataDiNascita());
        etTelefono.setText(user.getTelefono());

        int index = adapter.getPosition(user.getSesso());

        // Imposta la posizione selezionata usando la posizione ottenuta
        spSesso.setSelection(index);

        etDataNascita.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;
            boolean deletingHyphen;
            int hyphenStart;
            boolean deletingBackward;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                deletingBackward = count > after;
                if (deletingBackward && s.charAt(start) == '/') {
                    deletingHyphen = true;
                    hyphenStart = start;
                } else {
                    deletingHyphen = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No need to implement
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                if (deletingBackward && deletingHyphen) {
                    s.delete(hyphenStart, hyphenStart + 1);
                }

                // Rimuovi tutti i trattini correnti
                String originalText = s.toString().replaceAll("/", "");

                StringBuilder formattedText = new StringBuilder();
                int segmentLengths[] = {2, 2, 4}; // Lunghezza di ogni segmento

                int length = originalText.length();
                int totalSegments = segmentLengths.length;

                int segmentStart = 0;

                for (int i = 0; i < totalSegments; i++) {
                    int segmentLength = segmentLengths[i];
                    int segmentEnd = Math.min(segmentStart + segmentLength, length);
                    formattedText.append(originalText, segmentStart, segmentEnd);

                    if (segmentEnd < length) {
                        formattedText.append("/"); // Aggiungi il trattino
                    }

                    segmentStart = segmentEnd;
                }

                // Impedisci l'inserimento di ulteriori caratteri dopo il gruppo "cccc"
                if (formattedText.length() > 10) {
                    formattedText.setLength(10);
                }

                etDataNascita.removeTextChangedListener(this);
                etDataNascita.setText(formattedText.toString());
                etDataNascita.setSelection(formattedText.length());
                etDataNascita.addTextChangedListener(this);

                isFormatting = false;
            }
        });

        bttSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = etNome.getText().toString().trim();
                String cognome = etCognome.getText().toString().trim();
                String codiceFiscale = etCodiceFiscale.getText().toString().trim();
                String dataNascita = etDataNascita.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String sesso = spSesso.getSelectedItem().toString();

                if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || dataNascita.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(ChangePersonalActivity.this, "Tutti i campi devono essere compilati", Toast.LENGTH_LONG).show();
                } else {
                    PersonInformation personInformation = new PersonInformation(codiceFiscale, nome, cognome,dataNascita, telefono, sesso, "", "", "", "", "", user.getPIN());
                    dbPerson.updatePersonalInfo(personInformation);
                    Toast.makeText(ChangePersonalActivity.this, "Modifiche apportate con successo", Toast.LENGTH_SHORT).show();
                    Intent intentPro = new Intent(getString(R.string.LAUNCH_PROFILEACTIVITY))
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentPro);
                }
            }
        });

        bttAnnulla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPro = new Intent(getString(R.string.LAUNCH_PROFILEACTIVITY))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentPro);
                finish();
            }
        });

        bttHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentHome);
                finish();
            }
        });

        bttSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAlarm("Attivazione SOS Manuale");
            }
        });

        bttProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPro = new Intent(getString(R.string.LAUNCH_PROFILEACTIVITY))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentPro);
                finish();
            }
        });

    }

    private void callAlarm(String str){
        if(!isActivityStarted){
            Toast.makeText(ChangePersonalActivity.this, str, Toast.LENGTH_SHORT).show();
            Intent intentDetect = new Intent(getString(R.string.LAUNCH_DETECTIONACTIVITY));
            startActivity(intentDetect);
            isActivityStarted = true;
        }
    }

}
