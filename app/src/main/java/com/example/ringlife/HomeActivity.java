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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {
    private static final String API_KEY = "AIzaSyCmBdC1PB8lvsxFPlwBSMVrjafhB__H-eg";
    private GeoApiContext geoApiContext;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final int SOUND_THRESHOLD = 32500; // Soglia di volume per rilevare un suono forte (puoi regolarla in base alle tue esigenze)
    private static final float threshold = 200.0f; // Modifica la soglia a seconda delle tue esigenze
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private TextView tvHello, tvVelocita, tvAddress, tvCoordinate;
    private ImageButton bttSos;
    private GifImageView gifAmb;
    private String currentTextV, currentTextS, currentTextC, newTextV, newTextS, newTextC;
    private double latitude, longitude, latitudeSos, longitudeSos;
    private PersonData dbPerson;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float previousSpeed;
    private boolean accidentDetected;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gifAmb = findViewById(R.id.gifAmb);
        tvVelocita = findViewById(R.id.tvVelocita);
        tvAddress = findViewById(R.id.tvAddress);
        tvCoordinate = findViewById(R.id.tvCoordinate);
        tvHello = findViewById(R.id.tvHello);
        bttSos = findViewById(R.id.bttSos);
        currentTextV = tvVelocita.getText().toString();
        currentTextS = tvAddress.getText().toString();
        currentTextC = tvCoordinate.getText().toString();

        dbPerson = new PersonData(this);
        PersonInformation user = dbPerson.getPerson();

        tvHello.append(" " + user.getNome());


        List<String> missingPermissions = new ArrayList<>();

        // Verifica e aggiungi i permessi mancanti alla lista
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        // Verifica se ci sono permessi mancanti
        if (!missingPermissions.isEmpty()) {
            // Array dei permessi mancanti da richiedere
            String[] permissionsToRequest = missingPermissions.toArray(new String[0]);

            // Richiedi i permessi mancanti all'utente
            ActivityCompat.requestPermissions(this, permissionsToRequest, 1);
        } else {
            // Hai tutti i permessi necessari
            // Inizializza il servizio di localizzazione
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
            // Inizializza l'oggetto GeoApiContext con la tua API_KEY
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();

            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            previousSpeed = 0.0f;
            accidentDetected = false;
            startRecording();
        }

        bttSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSos = new Intent("com.example.ringlife.DetectionActivity.java");//Intent intentSos = new Intent(getString(R.string.LAUNCH_SOSACTIVITY));
                /*intentSos.putExtra("latitude", String.valueOf(latitudeSos));
                intentSos.putExtra("longitude", String.valueOf(longitudeSos));*/
                startActivity(intentSos);
            }
        });
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
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

    private void checkSoundLevel() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRecording) {
                    int amplitude = mediaRecorder.getMaxAmplitude();
                    if (amplitude > SOUND_THRESHOLD) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HomeActivity.this, "Rumore alto rilevato!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    try {
                        Thread.sleep(1000); // Controlla il livello di suono ogni secondo (puoi regolare l'intervallo in base alle tue esigenze)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

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



    @Override
    protected void onResume() {
        super.onResume();
        // Registra il listener di localizzazione quando l'applicazione è in primo piano
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

    private void changeData(double latitude, double longitude){
        latitudeSos = latitude;
        longitudeSos = longitude;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calcola l'accelerazione totale
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // Converti l'accelerazione totale in velocità approssimativa
            float speed = acceleration * 3.6f; // m/s to km/h

            // Calcola la variazione di velocità rispetto alla velocità precedente
            float speedChange = Math.abs(speed - previousSpeed);

            // Verifica se è stata rilevata una variazione significativa di velocità
            if (speedChange > threshold) {
                // Rilevato un possibile incidente
                accidentDetected = true;
                // Puoi eseguire qui le azioni appropriate, come inviare una notifica di emergenza o contattare i servizi di emergenza
                // Passare alla schermata di rischiesta "Stai bene?"
                Toast.makeText(this, "Possibile incidente rilevato!", Toast.LENGTH_SHORT).show();
            }

            previousSpeed = speed;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            new GetAddressTask().execute(latLng);
            // Ottieni la velocità di movimento in metri al secondo
            float speed = location.getSpeed();

            // Calcola la velocità in km/h
            float speedKmh = speed * 3.6f;

            // Visualizza la velocità nella TextView
            newTextV = currentTextV + String.format(Locale.getDefault(), "%.2f km/h", speedKmh);
            tvVelocita.setText(newTextV);
            newTextC = currentTextC + "\nLatitudine: " + latitude + "\nLongitudine: " + longitude;
            changeData(latitude, longitude);
            tvCoordinate.setText(newTextC);

        }

        private class GetAddressTask extends AsyncTask<LatLng, Void, String> {

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

                return address;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso accordato, puoi iniziare a utilizzare la localizzazione
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            } else {
                // Permesso negato, gestisci questa situazione adeguatamente
                Toast.makeText(HomeActivity.this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
