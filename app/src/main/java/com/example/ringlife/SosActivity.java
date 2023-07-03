package com.example.ringlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

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

        /*for(int i=0; i<numeriEmergenza.length; i++) {
            sms.sendTextMessage(numeriEmergenza[i], null, messaggio, null, null);
        }*/

        // se ha dei numeri di emergeza, chiama il primo numero
        /*if(numeriEmergenza.length > 0) {
            startCallToNumber(numeriEmergenza[currentEmergencyNumberIndex]);
        }*/

    }

    private void startCallToNumber(String number) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUGGGGGGGG", "avvio chiamata numero: "+ number + " [" + currentEmergencyNumberIndex + "]");
            currentEmergencyNumberIndex++;
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

    public BroadcastReceiver callEndedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DEBUGGGGGGGG", "RICEVUTO CALL_ENDED: "+ currentEmergencyNumberIndex + "\n" + intent.getAction());
            dbPerson = new PersonData(SosActivity.this);
            PersonInformation user = dbPerson.getPerson();
            String[] numeriEmergenza = user.getTelefoniEmergenza().split(",");

            // check if there are more numbers to call
            if (currentEmergencyNumberIndex < numeriEmergenza.length) {
                startCallToNumber(numeriEmergenza[currentEmergencyNumberIndex]);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DEBUGGGGGGGG", "INVIO CALL_ENDED: "+ this.getApplicationContext().registerReceiver(callEndedReceiver, new IntentFilter("com.example.ringlife.CALL_ENDED")));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.getApplicationContext().unregisterReceiver(callEndedReceiver);
        Log.d("DEBUGGGGGGGG", "PAUSA CALL_ENDED: "+ currentEmergencyNumberIndex);
    }
}
