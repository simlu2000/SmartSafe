package com.example.ringlife;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SosActivity extends AppCompatActivity {

    private TextView tvNome, tvCognome, tvDataNascita, tvPatologie, tvAllergie, tvGruppoSan, tvNumeriEmergenza;
    private String numero = "3496190159";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        /*tvNome = findViewById(R.id.tvNome);
        tvCognome = findViewById(R.id.tvCognome);
        tvDataNascita = findViewById(R.id.tvDataNascita);
        tvPatologie = findViewById(R.id.tvPatologie);
        tvAllergie = findViewById(R.id.tvAllergie);
        tvGruppoSan = findViewById(R.id.tvGruppoSan);
        tvNumeriEmergenza = findViewById(R.id.tvNumeriEmergenza);*/

        /*Intent callIntent =  new  Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse( "tel:" + numero));
        startActivity(callIntent);*/

        //Get the SmsManager instance and call the sendTextMessage method to send message
        SmsManager sms=SmsManager.getDefault();
        sms.sendTextMessage(numero, null, "Ciao fede", null,null);

    }
}
