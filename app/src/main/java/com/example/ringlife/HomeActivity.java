package com.example.ringlife;

import android.Manifest;import android.content.Context;
import android.content.pm.PackageManager;import android.hardware.Sensor;
import android.hardware.SensorEvent;import android.hardware.SensorEventListener;
import android.hardware.SensorManager;import android.os.Bundle;
import android.util.Log;import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

public class HomeActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView tvVelocita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvVelocita = findViewById(R.id.tvVelocita);

        // Verifica e richiedi il permesso per l'accesso alla posizione, se necessario
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Inizializza il servizio di localizzazione
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registra il listener di localizzazione quando l'applicazione è in primo piano
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Deregistra il listener di localizzazione quando l'applicazione è in background
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Ottieni la velocità di movimento in metri al secondo
        float speed = location.getSpeed();

        // Calcola la velocità in km/h
        float speedKmh = speed * 3.6f;

        // Visualizza la velocità nella TextView
        tvVelocita.setText(String.format(Locale.getDefault(), "Speed: %.2f km/h", speedKmh));
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // Ottieni la velocità di movimento in metri al secondo
            float speed = location.getSpeed();

            // Calcola la velocità in km/h
            float speedKmh = speed * 3.6f;

            // Visualizza la velocità nella TextView
            textViewSpeed.setText(String.format(Locale.getDefault(), "Speed: %.2f km/h", speedKmh));
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
