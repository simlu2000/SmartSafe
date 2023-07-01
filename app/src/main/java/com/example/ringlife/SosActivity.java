package com.example.ringlife;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class SosActivity extends AppCompatActivity {

    private TextView tvNome, tvCognome, tvDataNascita, tvPatologie, tvAllergie, tvGruppoSan, tvNumeriEmergenza;
    private String numero = "3518844529", messaggio, coordinate;
    private PersonData dbPerson;

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
        String numeriEmergenza = createStringSos(user.getContattoEmergenza(), user.getTelefoniEmergenza());
        tvNumeriEmergenza.append(numeriEmergenza);


        //Get the SmsManager instance and call the sendTextMessage method to send message
        SmsManager sms=SmsManager.getDefault();
        messaggio = "Messaggio generato da SmartSafe: ho bisogno di aiuto, sono qui: ";
        coordinate = "https://www.google.com/maps/search/?api=1&query=" + ((LocationData) getApplication()).getLatitude() + "," + ((LocationData) getApplication()).getLongitude();

        sms.sendTextMessage(numero, null, messaggio, null,null);
        sms.sendTextMessage(numero, null, coordinate, null,null);

        Intent callIntent =  new  Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse( "tel: 3518844529"));
        startActivity(callIntent);

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
}
