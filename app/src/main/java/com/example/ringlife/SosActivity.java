package com.example.ringlife;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class SosActivity extends AppCompatActivity {

    private TextView tvNome, tvCognome, tvDataNascita, tvPatologie, tvAllergie, tvGruppoSan, tvNumeriEmergenza, tvCodiceFiscale;
    private String messaggio, coordinate;
    private PersonData dbPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        tvNome = findViewById(R.id.tvNome);
        tvCognome = findViewById(R.id.tvCognome);
        tvDataNascita = findViewById(R.id.tvDataNascita);
        tvCodiceFiscale = findViewById(R.id.tvCodiceFiscale);
        tvPatologie = findViewById(R.id.tvPatologie);
        tvAllergie = findViewById(R.id.tvAllergie);
        tvGruppoSan = findViewById(R.id.tvGruppoSan);
        tvNumeriEmergenza = findViewById(R.id.tvNumeriEmergenza);

        // Ottieni il riferimento al WindowManager
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

        // Imposta il flag per mantenere l'attività sempre accesa
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        // Imposta il valore massimo per la luminosità dello schermo
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;

        // Applica le modifiche
        getWindow().setAttributes(layoutParams);

        dbPerson = new PersonData(SosActivity.this);
        PersonInformation user = dbPerson.getPerson();

        tvNome.append(user.getNome());
        tvCognome.append(user.getCognome());
        tvDataNascita.append(user.getDataDiNascita());
        tvCodiceFiscale.append(user.getCodiceFiscale());
        tvPatologie.append(user.getPatologie());
        tvAllergie.append(user.getAllergie());
        tvGruppoSan.append(user.getGruppoSanguigno());
        String[] numeriEmergenza = user.getTelefoniEmergenza().split(",");
        String contattiEmergenza = createStringSos(user.getContattoEmergenza(), user.getTelefoniEmergenza());
        tvNumeriEmergenza.append(contattiEmergenza);

        // Dichiara l'instanza SmsManager e avvia il metodo sendTextMessage per inviare
        // messaggi
        SmsManager sms = SmsManager.getDefault();
        coordinate = "https://www.google.com/maps/search/?api=1&query="
                + ((LocationData) getApplication()).getLatitude() + ","
                + ((LocationData) getApplication()).getLongitude();
        messaggio = "É UN TEST\n\n\nMessaggio generato da SmartSafe: ho bisogno di aiuto, sono qui: \n" + coordinate;

        for (int i = 0; i < numeriEmergenza.length; i++) {
            sms.sendTextMessage(numeriEmergenza[i], null, messaggio, null, null);
        }

        // se ha dei numeri di emergeza, chiama il numero prioritario
        if (numeriEmergenza.length > 0) {
            startCallToNumber(numeriEmergenza[0]);
        }

    }

    private void startCallToNumber(String number) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String createStringSos(String contattoEmergenza, String telefoniEmergenza) {
        String[] contatti = contattoEmergenza.split(",");
        String[] telefoni = telefoniEmergenza.split(",");
        String stringSos = "";
        for (int i = 0; i < contatti.length; i++) {
            if (i == 0)
                stringSos += " ";
            stringSos += contatti[i] + ": " + telefoni[i] + "\n";
        }
        return stringSos;
    }
}