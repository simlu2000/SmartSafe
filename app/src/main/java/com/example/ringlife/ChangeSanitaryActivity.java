package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class ChangeSanitaryActivity extends AppCompatActivity {

    /* Dichiarazione varibili ChangeSanitaryActivity Layout */
    private EditText etPatologie, etAllergie, etContattoEm1, etTelefonoEm1;
    private Spinner spGruppoSanguigno;
    private ArrayAdapter<CharSequence> adapter;
    private Button bttSalva, bttAnnulla, bttNuoviElementi;
    private ImageButton bttHome, bttSos, bttProfile;
    private PersonData dbPerson;
    private PersonInformation user;
    private LinearLayout lytContatti;
    private int maxCont = 0;
    private boolean isActivityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sanitary);

        lytContatti = findViewById(R.id.lytContatti);

        etPatologie = findViewById(R.id.etPatologie);
        etAllergie = findViewById(R.id.etAllergie);
        spGruppoSanguigno = findViewById(R.id.spGruppoSanguigno);
        etContattoEm1 = findViewById(R.id.etContattoEm1);
        etTelefonoEm1 = findViewById(R.id.etTelefonoEm1);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.GruppoSanguigno, android.R.layout.simple_spinner_item);
        bttNuoviElementi = findViewById(R.id.bttNuoviElementi);
        bttSalva = findViewById(R.id.bttSavePersonal);
        bttAnnulla = findViewById(R.id.bttBackPersonal);
        bttHome = findViewById(R.id.bttHome);
        bttSos = findViewById(R.id.bttSos);
        bttProfile = findViewById(R.id.bttProfile);

        dbPerson = new PersonData(this);
        user = dbPerson.getPerson();

        etPatologie.setText(user.getPatologie());
        etAllergie.setText(user.getAllergie());

        int index = adapter.getPosition(user.getGruppoSanguigno());
        spGruppoSanguigno.setSelection(index);

        String[] contattiEmergenza = user.getContattoEmergenza().split(",");
        String[] numeriEmergenza = user.getTelefoniEmergenza().split(",");
        maxCont = contattiEmergenza.length;
        if(contattiEmergenza.length == 1 && numeriEmergenza.length == 1){
            etContattoEm1.setText(contattiEmergenza[0]);
            etTelefonoEm1.setText(numeriEmergenza[0]);
        }else{
            if(contattiEmergenza.length > 1 && numeriEmergenza.length > 1){
                for (int i = 1; i < contattiEmergenza.length; i++) {
                    createNewTextViewAndEditText(lytContatti, i+1, true, contattiEmergenza[i], numeriEmergenza[i]);

                }
            }else{
                Toast.makeText(this, "Errore lunghezza contatti", Toast.LENGTH_SHORT).show();
            }
        }

       bttNuoviElementi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maxCont<5){
                    createNewTextViewAndEditText(lytContatti, maxCont+1, false, "", "");
                    maxCont++;
                }else{
                    Toast.makeText(ChangeSanitaryActivity.this, "Non puoi aggiungere più numeri di telefono", Toast.LENGTH_SHORT).show();
                }

            }
        });

        bttSalva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*String nome = etNome.getText().toString().trim();
                String cognome = etCognome.getText().toString().trim();
                String codiceFiscale = etCodiceFiscale.getText().toString().trim();
                String dataNascita = etDataNascita.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String sesso = spSesso.getSelectedItem().toString();

                if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || dataNascita.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(ChangeSanitaryActivity.this, "Tutti i campi devono essere compilati", Toast.LENGTH_LONG).show();
                } else {
                    PersonInformation personInformation = new PersonInformation(codiceFiscale, nome, cognome,dataNascita, telefono, sesso, "", "", "", "", "", user.getPIN());
                    dbPerson.updatePersonalInfo(personInformation);
                    Toast.makeText(ChangeSanitaryActivity.this, "Modifiche apportate con successo", Toast.LENGTH_SHORT).show();
                    Intent intentPro = new Intent(getString(R.string.LAUNCH_PROFILEACTIVITY))
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentPro);
                }*/
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

    private void createNewTextViewAndEditText(LinearLayout layout, int n, boolean call, String contatto, String numero){
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

        if(call){
            editTextContattoEm.setHint(contatto);
            editTextTelefonoEm.setHint(numero);

        }

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

    private void callAlarm(String str){
        if(!isActivityStarted){
            Toast.makeText(ChangeSanitaryActivity.this, str, Toast.LENGTH_SHORT).show();
            Intent intentDetect = new Intent(getString(R.string.LAUNCH_DETECTIONACTIVITY));
            startActivity(intentDetect);
            isActivityStarted = true;
        }
    }
}
