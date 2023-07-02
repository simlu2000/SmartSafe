package com.example.ringlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class SosActivity extends AppCompatActivity {

    private TextView tvNome, tvCognome, tvDataNascita, tvPatologie, tvAllergie, tvGruppoSan, tvNumeriEmergenza;
    private String messaggio, coordinate;
    private PersonData dbPerson;
    private int currentEmergencyNumberIndex = 0; // keep track of the current emergency number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        tvNome = findViewById(R.id.tvNome);
        tvCognome = findViewById(R.id.tvCognome);
        tvDataNascita = findViewById(R.id.tvDataNascita);
        tvPatologie = findViewById(R.id.tvPatologie);
        tvAllergie = findViewById(R.id.tvAllergie);
        tvGruppoSan = findViewById(R.id.tvGruppoSan);
        tvNumeriEmergenza = findViewById(R.id.tvNumeriEmergenza);


        dbPerson = new PersonData(SosActivity.this);
        PersonInformation user = dbPerson.getPerson();

        tvNome.append(user.getNome());
        tvCognome.append(user.getCognome());
        tvDataNascita.append(user.getDataDiNascita());
        tvPatologie.append(user.getPatologie());
        tvAllergie.append(user.getAllergie());
        tvGruppoSan.append(user.getGruppoSanguigno());
        String[] numeriEmergenza = user.getTelefoniEmergenza().split(",");
        String contattiEmergenza = createStringSos(user.getContattoEmergenza(), user.getTelefoniEmergenza());
        tvNumeriEmergenza.append(contattiEmergenza);


        //Get the SmsManager instance and call the sendTextMessage method to send message
        SmsManager sms = SmsManager.getDefault();
        coordinate = "https://www.google.com/maps/search/?api=1&query=" + ((LocationData) getApplication()).getLatitude() + "," + ((LocationData) getApplication()).getLongitude();
        messaggio = "É UN TEST\n\n\nMessaggio generato da SmartSafe: ho bisogno di aiuto, sono qui: \n" + coordinate;

        for(int i=0; i<numeriEmergenza.length; i++) {
            sms.sendTextMessage(numeriEmergenza[i], null, messaggio, null, null);
        }

        // if we have emergency numbers, start a call to the first number
        if(numeriEmergenza.length > 0) {
            startCallToNumber(numeriEmergenza[currentEmergencyNumberIndex]);
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
    public void onBackPressed(){
        Intent intent = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private String createStringSos(String contattoEmergenza, String telefoniEmergenza){
        String[] contatti = contattoEmergenza.split(",");
        String[] telefoni = telefoniEmergenza.split(",");
        String stringSos = "";
        for(int i = 0; i < contatti.length; i++){
            stringSos += contatti[i] + ": " + telefoni[i] + "\n";
        }
        return stringSos;
    }

    private BroadcastReceiver callEndedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            dbPerson = new PersonData(SosActivity.this);
            PersonInformation user = dbPerson.getPerson();
            String[] numeriEmergenza = user.getTelefoniEmergenza().split(",");

            // check if there are more numbers to call
            if (currentEmergencyNumberIndex + 1 < numeriEmergenza.length) {
                currentEmergencyNumberIndex++;
                startCallToNumber(numeriEmergenza[currentEmergencyNumberIndex]);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(callEndedReceiver, new IntentFilter("com.example.app.CALL_ENDED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(callEndedReceiver);
    }
}
