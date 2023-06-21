package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private final String TAG = "RegisterActivity";
    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita, etTelefono, etPatologie, etAllergie, etContattoEm, etPin, etTelefonoEm;
    private Spinner spSesso, spGruppoSanguigno;
    private Button bttConferma, bttNuoviElementi;
    private PersonData dbPerson;
    private LinearLayout lytDatiMedici;
    private int maxCont = 0;

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
        etTelefonoEm = findViewById(R.id.etTelefonoEm);
        etPin = findViewById(R.id.etPin);

        spSesso = findViewById(R.id.spSesso);
        spGruppoSanguigno = findViewById(R.id.spGruppoSanguigno);

        lytDatiMedici = findViewById(R.id.lytDatiMedici);

        bttNuoviElementi = findViewById(R.id.bttNuoviElementi);
        bttConferma = findViewById(R.id.bttConferma);

        Log.i(TAG, "Prima della funzione");
        insertInDB();
        Log.i(TAG, "Dopo la funzione");
        etDataNascita.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                try {
                    String input = s.toString();
                    if (input.length() == 2 || input.length() == 5) {
                        String formattedDate = formatDateString(input);
                        etDataNascita.setText(formattedDate);
                        etDataNascita.setSelection(formattedDate.length());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                isFormatting = false;
            }

            private String formatDateString(String input) throws ParseException {
                String formattedDate = input;
                if (input.length() == 2 || input.length() == 5) {
                    formattedDate += "/";
                }
                return dateFormat.format(dateFormat.parse(formattedDate));
            }
        });

        bttNuoviElementi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maxCont<5){
                    createNewTextViewAndEditText(lytDatiMedici, "etContattoEm" + n+1, "etTelefonoEm" + n+1);
                    maxCont++;
                }else{
                    Toast.makeText(RegisterActivity.this, "Non puoi aggiungere più numeri di telefono", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createNewTextViewAndEditText(LinearLayout layout, String contEm, String telEm) {
        // create a new EditText to emergency contact
        EditText editTextContEm = new EditText(RegisterActivity.this);
        editTextContEm.setLayoutParams(new LinearLayout.LayoutParams(
                160,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editTextContEm.setHint("Contatto d'emergenza");
        editTextContEm.setId("@+id/" + contEm);

        // create a new EditText to emergency phone
        EditText editTextTelEm = new EditText(RegisterActivity.this);
        editTextTelEm.setLayoutParams(new LinearLayout.LayoutParams(
                160,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editTextTelEm.setHint("Telefono d'emergenza");
        editTextTelEm.setId("@+id/" + telEm);
        editTextTelEm.setInputType(InputType.TYPE_CLASS_PHONE);

        // add the textview and edittext to the linearlayout
        layout.addView(editTextContEm);
        layout.addView(editTextTelEm);
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
                String telefonoEm = getAllTelEm();
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
                        PersonInformation personInformation = new PersonInformation(codiceFiscale, nome, cognome,dataNascita, telefono, sesso, gruppoSanguigno, patologie, allergie, contattoEm, telefonoEm, pin);
                        dbPerson.addPerson(personInformation);

                        Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                        startActivity(intentHome);
                    }
                }
            }
        });
    }

    //Fare da qui
    private String getAllTelEm(){
        return "ciao";
    }
}
