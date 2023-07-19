package com.example.ringlife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {
    /* Dichiarazione varibili HomeActivity Layout */
    private TextView tvHello, tvVelocita, tvAddress, tvCoordinate;
    private ImageButton bttSos, bttProfile;
    private String currentTextV, currentTextS, currentTextC, newTextV, newTextS, newTextC;
    /* Dichiarazione varibili API Posizione */
    private static final String API_KEY = "AIzaSyCmBdC1PB8lvsxFPlwBSMVrjafhB__H-eg";
    private GeoApiContext geoApiContext;
    private LocationManager locationManager;
    private LocationListener locationListener;
    /* Dichiarazione varibili Rilevamento Incidente */
    // INIZIALIZZAZIONE VETTORI DEI 3 SENSORI
    public LimitVector<Float> gpsData = new LimitVector<Float>(3);
    public LimitVector<Float> accData = new LimitVector<Float>(3);
    public LimitVector<Float> micrData = new LimitVector<Float>(10);
    // INIZIALIZZARE I 3 FLAG
    public int gpsFlag = 0;
    public int accFlag = 0;
    public int micrFlag = 0;
    private float mediaPrec = 0;
    private static final float SOUND_THRESHOLD = 18500; // Soglia di volume per rilevare un suono forte (puoi regolarla in base all'esigenze)
    private static final float ACC_THRESHOLD = (float)(-4*9.8); // Soglia di una frenata brusca
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    /* Dichiarazione altre varibili */
    private boolean isActivityStarted = false;
    private PersonData dbPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvVelocita = findViewById(R.id.tvVelocita);
        tvAddress = findViewById(R.id.tvAddress);
        tvCoordinate = findViewById(R.id.tvCoordinate);
        tvHello = findViewById(R.id.tvHello);
        bttSos = findViewById(R.id.bttSos);
        bttProfile = findViewById(R.id.bttProfile);
        currentTextV = tvVelocita.getText().toString();
        currentTextS = tvAddress.getText().toString();
        currentTextC = tvCoordinate.getText().toString();

        //Accesso al db
        dbPerson = new PersonData(this);
        PersonInformation user = dbPerson.getPerson();

        tvHello.append(" " + user.getNome());

        //Visualizzazione velocità e posizione
        newTextV = currentTextV + String.format(Locale.getDefault(), "%.2f km/h", ((LocationData) getApplication()).getVelocita());
        tvVelocita.setText(newTextV);
        newTextC = currentTextC + "\nLatitudine: " + ((LocationData) getApplication()).getLatitude() + "\nLongitudine: " + ((LocationData) getApplication()).getLongitude();
        tvCoordinate.setText(newTextC);
        if(!((LocationData) getApplication()).getAddress().isEmpty()) {
            newTextS = currentTextS + ((LocationData) getApplication()).getAddress();
            tvAddress.setText(newTextS);
        }

        checkLocationSettings();
        // Hai tutti i permessi necessari
        // Inizializza il servizio di localizzazione
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        // Permesso accordato, puoi iniziare a utilizzare la localizzazione
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Inizializzo tutti gli oggetti utili per velocità, posizionamento
        // e rilevamento incidenti
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Inizio salvataggio dati AUDIO
        startRecording();


        bttSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDetection("Attivazione SOS Manuale");
            }
        });

        bttProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfile = new Intent(getString(R.string.LAUNCH_PROFILEACTIVITY));
                startActivity(intentProfile);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Registra il listener di localizzazione quando l'applicazione è in primo piano
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        sensorManager.registerListener(this, accelerometer, 1000000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Deregistra il listener di localizzazione quando l'applicazione è in background
        locationManager.removeUpdates(locationListener);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    // Funzione che controlla attivazione GPS
    private void checkLocationSettings() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Verifica se il GPS è attivo
        if (!isGpsEnabled) {
            Toast.makeText(this, "Attiva Geocalizzazione", Toast.LENGTH_SHORT).show();
            // Il GPS non è attivo, richiedi all'utente di attivarlo
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
    private void azzeraFlag(){
        accFlag = 0;
        gpsFlag = 0;
        micrFlag = 0;
    }
    private void controlloFlag(){
        int tot = accFlag + gpsFlag;if(tot>0 && tot<3 && micrFlag>1){
            azzeraFlag();
            callDetection("Probabilità incidente medio");
        } else if (tot>2 && tot<5) {
            azzeraFlag();
            if(micrFlag<2){
                if(!(accFlag == 0 || gpsFlag == 0) && micrFlag != 0)
                    callDetection("Probabilità incidente medio");
            }else {
                callSOS("Probabilità incidente forte");
            }
        }else if(tot>4){
            azzeraFlag();
            if(micrFlag==0){
                callDetection("Probabilità incidente medio");
            }else {
                callSOS("Probabilità incidente forte");
            }
        }
    }
    private void callDetection(String str){
        if(!isActivityStarted){
            Toast.makeText(HomeActivity.this, str, Toast.LENGTH_LONG).show();
            Intent intentDetect = new Intent(getString(R.string.LAUNCH_DETECTIONACTIVITY));
            startActivity(intentDetect);
            isActivityStarted = true;
        }
    }
    private void callSOS(String str){
        if(!isActivityStarted){
            Toast.makeText(HomeActivity.this, str, Toast.LENGTH_LONG).show();
            Intent intentDetect = new Intent(getString(R.string.LAUNCH_SOSACTIVITY));
            startActivity(intentDetect);
            isActivityStarted = true;
        }
    }

    // Inizio registrazione telefono
    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //mediaRecorder.setAudioSamplingRate(1000000);
        String outputFile = getFilesDir().getAbsolutePath() + "/audio.3gp";
        mediaRecorder.setOutputFile(outputFile);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            checkSoundLevel();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error starting recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Controllo di quanto sia forte un rumore
    private void checkSoundLevel() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRecording) {

                    micrData.addElementAtBeginning((float)mediaRecorder.getMaxAmplitude());
                    if(mediaPrec==0) {
                        mediaPrec = micrData.media();
                    }else{
                        float media = micrData.media();
                        if ((((media-mediaPrec)/mediaPrec)*100) >= 40 && media > SOUND_THRESHOLD) {
                            if (micrFlag < 3) micrFlag++;
                        } else if(micrFlag>0 && media > SOUND_THRESHOLD){
                            if (micrFlag < 3) micrFlag++;
                        }else {
                            if (micrFlag > 0) micrFlag--;
                        }

                        mediaPrec = media;
                    }
                    try {
                        Thread.sleep(100); // Controlla il livello di suono ogni secondo (puoi regolare l'intervallo in base alle tue esigenze)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    // Interruzione registrazione
    private void stopRecording() {
        if (mediaRecorder != null && isRecording) {
            isRecording = false;
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Cambiamento valori sensori
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcola l'accelerazione totale
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            //AGGIUNTA NEL VETTORE DELL'ACCELLERAZIONE AL METRO/SECONDO^2
            accData.addElementAtBeginning(acceleration);
            if(accData.size() > 1) {
                float delta = accData.deltaMedio();

                if ((acceleration < 10 && acceleration > 9) && delta < ACC_THRESHOLD){
                    accFlag=3;
                    controlloFlag();
                } else if (delta < ACC_THRESHOLD) {
                    if (accFlag < 3) accFlag++;
                    controlloFlag();
                } else {
                    if (accFlag > 0) accFlag--;
                }
                Log.d("ACCELLERAZIONE", "accData: " + accData + " Delta: " + delta + " AccFlag: " + accFlag);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Funzione che prendre la posizione + velocità utente
    private class MyLocationListener implements LocationListener {
        //Assegnamento velocità dell'utente
        @Override
        public void onLocationChanged(Location location) {
            ((LocationData) getApplication()).setLatitude(location.getLatitude());
            ((LocationData) getApplication()).setLongitude(location.getLongitude());
            double latitude = ((LocationData) getApplication()).getLatitude();
            double longitude = ((LocationData) getApplication()).getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            new GetAddressTask().execute(latLng);
            // Ottieni la velocità di movimento in metri al secondo
            float speed = location.getSpeed();


            // Salvo il calcolo della velocità in km/h in una Var globale
            ((LocationData) getApplication()).setVelocita((speed * 3.6f));

            // Ottenere il valore della velocità globale
            float speedKmh = ((LocationData) getApplication()).getVelocita();

            // Visualizza la velocità nella TextView
            newTextV = currentTextV + String.format(Locale.getDefault(), "%.2f km/h", speedKmh);
            tvVelocita.setText(newTextV);
            newTextC = currentTextC + "\nLatitudine: " + latitude + "\nLongitudine: " + longitude;
            tvCoordinate.setText(newTextC);

            //AGGIUNTA NEL VETTORE DELLA VELOCITA' AL METRO/SECONDO^2
            gpsData.addElementAtBeginning((float)speed);
            if(gpsData.size() > 1) {
                float delta = gpsData.deltaMedio();

                if (speed == 0 && delta < ACC_THRESHOLD){
                    gpsFlag=3;
                    controlloFlag();
                } else if (delta < ACC_THRESHOLD) {
                    if (gpsFlag < 3) gpsFlag++;
                    controlloFlag();
                } else {
                    if (gpsFlag > 0) gpsFlag--;
                }
                Log.d("GPS", "gpsData: " + gpsData + " Delta: " + delta + " GPSFlag: " + gpsFlag);
            }
        }

        private class GetAddressTask extends AsyncTask<LatLng, Void, String> {
            //Assegnamento posizione dell'utente
            @Override
            protected String doInBackground(LatLng... latLngs) {
                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                LatLng latLng = latLngs[0];
                List<Address> addresses;
                String address = "";

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        Address fetchedAddress = addresses.get(0);
                        address = fetchedAddress.getAddressLine(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Salvo l'indirizzo in una Var globale
                ((LocationData) getApplication()).setAddress(address);

                // Ottenere il valore dell'indirizzo globale
                return ((LocationData) getApplication()).getAddress();
            }
            @Override
            protected void onPostExecute(String address) {
                if (address != null && !address.isEmpty()) {
                    newTextS = currentTextS + address;
                    tvAddress.setText(newTextS);
                } else {
                    tvAddress.setText(currentTextS + " Sconosciuta");
                }
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Implementa il tuo codice qui
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Implementa il tuo codice qui
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Implementa il tuo codice qui
        }
    }
}