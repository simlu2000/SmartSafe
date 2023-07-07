package com.example.ringlife;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.Arrays;

public class ChangeSanitaryActivity extends AppCompatActivity {

    /* Dichiarazione varibili ChangeSanitaryActivity Layout */
    private EditText etPatologie, etAllergie, etContattoEm1, etTelefonoEm1, etContattoEm2, etTelefonoEm2, etContattoEm3, etTelefonoEm3, etContattoEm4, etTelefonoEm4, etContattoEm5, etTelefonoEm5;
    private Spinner spGruppoSanguigno;
    private ArrayAdapter<CharSequence> adapter;
    private Button bttSalva, bttAnnulla, bttNuoviElementi;
    private ImageButton bttHome, bttSos, bttProfile,deleteContact1,deleteContact2,deleteContact3,deleteContact4,deleteContact5;
    private PersonData dbPerson;
    private PersonInformation user;
    private LinearLayout lytContatti, lytSanitaryInfo;
    private int maxCont = 0;
    private boolean isActivityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sanitary);

        lytContatti = findViewById(R.id.lytContatti);
        lytSanitaryInfo = findViewById(R.id.lytSanitaryInfo);
        etPatologie = findViewById(R.id.etPatologie);
        etAllergie = findViewById(R.id.etAllergie);
        spGruppoSanguigno = findViewById(R.id.spGruppoSanguigno);
        etContattoEm1 = findViewById(R.id.etContattoEm1);
        etTelefonoEm1 = findViewById(R.id.etTelefonoEm1);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.GruppoSanguigno, android.R.layout.simple_spinner_item);
        deleteContact1 = findViewById(R.id.deleteContact1);
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
                etContattoEm1.setText(contattiEmergenza[0]);
                etTelefonoEm1.setText(numeriEmergenza[0]);
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
                String patologie = etPatologie.getText().toString();
                String allergie = etAllergie.getText().toString();
                String gruppoSanguigno = spGruppoSanguigno.getSelectedItem().toString();
                String contatto1 = "", contatto2 = "", contatto3 = "", contatto4 = "", contatto5= "";
                String numero1 = "", numero2 = "", numero3 = "", numero4 = "", numero5= "";

                contatto1 = etContattoEm1.getText().toString();
                numero1 = etTelefonoEm1.getText().toString();
                if(etContattoEm2 != null && etTelefonoEm2 != null){
                    contatto2 = etContattoEm2.getText().toString();
                    numero2 = etTelefonoEm2.getText().toString();
                }
                if(etContattoEm3 != null && etTelefonoEm3 != null){
                    contatto3 = etContattoEm3.getText().toString();
                    numero3 = etTelefonoEm3.getText().toString();
                }
                if(etContattoEm4 != null && etTelefonoEm4 != null){
                    contatto4 = etContattoEm4.getText().toString();
                    numero4 = etTelefonoEm4.getText().toString();
                }
                if(etContattoEm5 != null && etTelefonoEm5 != null){
                    contatto5 = etContattoEm5.getText().toString();
                    numero5 = etTelefonoEm5.getText().toString();
                }

                if (patologie.isEmpty() || allergie.isEmpty() || (contatto1.isEmpty() && contatto2.isEmpty() &&contatto3.isEmpty() &&contatto4.isEmpty() &&contatto5.isEmpty())) {
                    Toast.makeText(ChangeSanitaryActivity.this, "Tutti i campi devono essere compilati", Toast.LENGTH_LONG).show();
                } else {
                    String contatti = concatena(contatto1, contatto2, contatto3, contatto4, contatto5);
                    String numeri = concatena(numero1, numero2, numero3, numero4, numero5);
                    PersonInformation personInformation = new PersonInformation("", "", "", "", "", "", gruppoSanguigno, patologie, allergie, contatti, numeri, user.getPIN());
                    dbPerson.updateSanitaryInfo(personInformation);
                    Toast.makeText(ChangeSanitaryActivity.this, "Modifiche apportate con successo", Toast.LENGTH_SHORT).show();
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

        deleteContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etContattoEm1.setText("");
                etTelefonoEm1.setText("");
            }
        });
    }

    private void createNewTextViewAndEditText(LinearLayout layout, int n, boolean call, String contatto, String numero){
        // Crea il nuovo LinearLayout
        LinearLayout newLinearLayout = new LinearLayout(this);
        newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLinearLayout.setTag("box" + n);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(0, 10, 0, 0);
        newLinearLayout.setLayoutParams(layoutParams);

        // Crea il primo EditText
        EditText contattoEditText = new EditText(this);
        LinearLayout.LayoutParams contattoParams = new LinearLayout.LayoutParams(
                dpToPx(160), // Usa la funzione dpToPx per convertire i dp in pixel
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contattoParams.setMargins(dpToPx(-5), 0, dpToPx(25), 0);
        contattoEditText.setTag("etContattoEm" + n);
        contattoEditText.setLayoutParams(contattoParams);
        contattoEditText.setHint("Contatto \nd'emergenza*");
        contattoEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        // Crea il secondo EditText
        EditText telefonoEditText = new EditText(this);
        LinearLayout.LayoutParams telefonoParams = new LinearLayout.LayoutParams(
                dpToPx(120), // Usa la funzione dpToPx per convertire i dp in pixel
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        telefonoParams.setMargins(dpToPx(-15), dpToPx(10), 0, 0);
        telefonoEditText.setTag("etTelefonoEm" + n);
        telefonoEditText.setLayoutParams(telefonoParams);
        telefonoEditText.setHint("Telefono");
        telefonoEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        // Crea l'ImageView
        ImageButton deleteImageView = new ImageButton(this);
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        imageViewParams.setMargins(dpToPx(0), dpToPx(15), 0, 0);
        deleteImageView.setTag("deleteContact" + n);
        deleteImageView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        deleteImageView.setLayoutParams(imageViewParams);
        deleteImageView.setImageResource(R.drawable.trash);

        if(call){
            contattoEditText.setText(contatto);
            telefonoEditText.setText(numero);
        }

        // Aggiungi tutti gli elementi al LinearLayout
        newLinearLayout.addView(contattoEditText);
        newLinearLayout.addView(telefonoEditText);
        newLinearLayout.addView(deleteImageView);

        // Infine, aggiungi il nuovo LinearLayout al LinearLayout padre
        layout.addView(newLinearLayout);

        String tag = "deleteContact" + n;
        if(n==2){
            etContattoEm2 = contattoEditText;
            etTelefonoEm2 = telefonoEditText;
            deleteContact2 = (ImageButton) layout.findViewWithTag(tag);
            deleteContact2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contattoEditText.setText("");
                    telefonoEditText.setText("");
                }
            });
        } else if (n==3) {
            etContattoEm3 = contattoEditText;
            etTelefonoEm3 = telefonoEditText;
            deleteContact3 = (ImageButton) layout.findViewWithTag(tag);
            deleteContact3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contattoEditText.setText("");
                    telefonoEditText.setText("");
                }
            });
        } else if (n==4) {
            etContattoEm4 = contattoEditText;
            etTelefonoEm4 = telefonoEditText;
            deleteContact4 = (ImageButton) layout.findViewWithTag(tag);
            deleteContact4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contattoEditText.setText("");
                    telefonoEditText.setText("");
                }
            });
        } else if (n==5) {
            etContattoEm5 = contattoEditText;
            etTelefonoEm5 = telefonoEditText;
            deleteContact5 = (ImageButton) layout.findViewWithTag(tag);
            deleteContact5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contattoEditText.setText("");
                    telefonoEditText.setText("");
                }
            });
        }
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

    private String concatena(String s1, String s2, String s3, String s4, String s5){
        String[] arr = {s1, s2, s3, s4, s5};
        String[] newarr = Arrays.stream(arr)
                .filter(s -> (s != null && !s.isEmpty()))
                .toArray(String[]::new);
        String s = "";
        for (int i=0; i< newarr.length; i++){
            if(i==0)
                s += newarr[i];
            else
                s += ", " + newarr[i];
        }
        return s;
    }

}
