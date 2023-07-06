package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class RegisterActivity extends AppCompatActivity {
    private final String TAG = "RegisterActivity";

    /* Dichiarazione varibili RegisterActivity Layout */
    private EditText etNome, etCognome, etCodiceFiscale, etDataNascita, etTelefono, etPatologie, etAllergie, etContattoEm, etPin, etTelefonoEm;
    private Spinner spSesso, spGruppoSanguigno;
    private Button bttConferma, bttNuoviElementi;
    private PersonData dbPerson;
    private LinearLayout lytContatti;
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

        lytContatti = findViewById(R.id.lytContatti);

        bttNuoviElementi = findViewById(R.id.bttNuoviElementi);
        bttConferma = findViewById(R.id.bttConferma);

        dbPerson = new PersonData(RegisterActivity.this);

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

        bttNuoviElementi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maxCont<4){
                    createNewTextViewAndEditText(lytContatti, maxCont+1);
                    maxCont++;
                }else{
                    Toast.makeText(RegisterActivity.this, "Non puoi aggiungere più numeri di telefono", Toast.LENGTH_SHORT).show();
                }

            }
        });

        insertInDB();
    }
    // Creazione nuovi campi per inserimento di nome e numero
    private void createNewTextViewAndEditText(LinearLayout layout, int n){
        //create names id for edit text and text view
        LinearLayout lytContTel = new LinearLayout(this);
        lytContTel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        lytContTel.setOrientation(LinearLayout.HORIZONTAL);
        lytContTel.setGravity(Gravity.CENTER_HORIZONTAL);
        lytContTel.setPadding(0, 10, 0, 0);



        //create edit text contatto emergenza
        EditText editTextContattoEm = new EditText(this);
        editTextContattoEm.setId(View.generateViewId());
        editTextContattoEm.setTag("etContattoEm" + n);
        editTextContattoEm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editTextContattoEm.setHint("Contatto \nd'emergenza*");
        editTextContattoEm.setLayoutParams(new LinearLayout.LayoutParams(
                dpToPx(160), // Larghezza fissa di 160dp
                LinearLayout.LayoutParams.WRAP_CONTENT));

        //create edit text telefono emergenza
        EditText editTextTelefonoEm = new EditText(this);
        editTextTelefonoEm.setId(View.generateViewId());
        editTextTelefonoEm.setTag("etTelefonoEm" + n);
        editTextTelefonoEm.setHint("Telefono");
        editTextTelefonoEm.setInputType(InputType.TYPE_CLASS_PHONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dpToPx(120), // Width of 160dp
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(25), dpToPx(10), 0, 0);
        editTextTelefonoEm.setLayoutParams(params);

        //add edit text to layout
        lytContTel.addView(editTextContattoEm);
        lytContTel.addView(editTextTelefonoEm);

        layout.addView(lytContTel);
    }
    // Conversione da dp a px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    // Inserimento su db
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
                String contattoEm = getContattoEm(lytContatti);
                String telefonoEm = getTelefonoEm(lytContatti);
                String pin = etPin.getText().toString().trim();
                String sesso = spSesso.getSelectedItem().toString();
                String gruppoSanguigno = spGruppoSanguigno.getSelectedItem().toString();

                //Toast.makeText(RegisterActivity.this, "Contatto: " + contattoEm + " Telefono: " + telefonoEm, Toast.LENGTH_LONG).show();

                if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || dataNascita.isEmpty() ||
                        telefono.isEmpty() || patologie.isEmpty() || allergie.isEmpty() || contattoEm.isEmpty()
                        || pin.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Tutti i campi devono essere compilati", Toast.LENGTH_LONG).show();
                } else {
                    if(pin.length() < 5 && pin.matches("[0-9.]+"))
                        Toast.makeText(RegisterActivity.this, "Inserisci un PIN con almeno 5 cifre e/o deve contenere solo numeri", Toast.LENGTH_LONG).show();
                    else{
                        PersonInformation personInformation = new PersonInformation(codiceFiscale, nome, cognome,dataNascita, telefono, sesso, gruppoSanguigno, patologie, allergie, contattoEm, telefonoEm, pin);
                        dbPerson.addPerson(personInformation);

                        Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY))
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentHome);
                    }
                }
            }
        });
    }

    // Acquisizione nome persona per salvataggio su db
    private String getContattoEm(LinearLayout layout){
        String contattoEm = etContattoEm.getText().toString();
        if(maxCont==0)
            return contattoEm;
        else{
            for(int i=1; i<=maxCont; i++){
                String editTextTag = "etContattoEm" + i;
                EditText editText = layout.findViewWithTag(editTextTag);

                if(editText != null)
                    contattoEm = contattoEm + ", " + editText.getText().toString();
            }

            return contattoEm;
        }
    }

    // Acquisizione numero persona per salvataggio su db
    private String getTelefonoEm(LinearLayout layout){
        String telefonoEm = etTelefonoEm.getText().toString();
        if(maxCont==0)
            return telefonoEm;
        else{
            for(int i=1; i<=maxCont; i++){
                String editTextTag = "etTelefonoEm" + i;
                EditText editText = layout.findViewWithTag(editTextTag);

                if(editText != null)
                    telefonoEm = telefonoEm + ", " + editText.getText().toString();
            }

            return telefonoEm;
        }
    }
}
