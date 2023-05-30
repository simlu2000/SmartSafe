package com.example.ringlife;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final String API_KEY = "AIzaSyCmBdC1PB8lvsxFPlwBSMVrjafhB__H-eg";
    private GeoApiContext geoApiContext;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView tvVelocita, tvAddress, tvCoordinate;
    private ImageButton bttSos;
    private String currentTextV, currentTextS, currentTextC, newTextV, newTextS, newTextC;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvVelocita = findViewById(R.id.tvVelocita);
        tvAddress = findViewById(R.id.tvAddress);
        tvCoordinate = findViewById(R.id.tvCoordinate);
        bttSos = findViewById(R.id.bttSos);
        currentTextV = tvVelocita.getText().toString();
        currentTextS = tvAddress.getText().toString();
        currentTextC = tvCoordinate.getText().toString();



        // Verifica e richiedi il permesso per l'accesso alla posizione, se necessario
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Inizializza il servizio di localizzazione
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
        }

        // Inizializza l'oggetto GeoApiContext con la tua API_KEY
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        accessSosScreen();
    }

    private void accessSosScreen(){
        bttSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getString(R.string.LAUNCH_SOSACTIVITY));
                startActivity(intent);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Deregistra il listener di localizzazione quando l'applicazione è in background
        locationManager.removeUpdates(locationListener);
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }
            } else {
                // Permesso negato, gestisci questa situazione adeguatamente
                Toast.makeText(HomeActivity.this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
}
