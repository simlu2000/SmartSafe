package com.example.ringlife;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.ringlife.Database.PersonData;
import com.example.ringlife.PersonInformation.PersonInformation;

public class MainActivity extends AppCompatActivity {

    private Button bttInUp;
    private PersonData dbPerson;
    private boolean exist;
    private String action;
    private EditText etPin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bttInUp = findViewById(R.id.bttInUp);
        etPin = findViewById(R.id.etPin);
        etPin.setVisibility(View.INVISIBLE);

        dbPerson = new PersonData(this);
        exist = dbPerson.ifExistPerson();

        if(exist){
            etPin.setVisibility(View.VISIBLE);
            bttInUp.setVisibility(View.INVISIBLE);
            PersonInformation user = dbPerson.getPerson();
            etPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL){
                        String getPin = etPin.getText().toString();
                        if (getPin.matches("")) {
                            Toast.makeText(MainActivity.this, "Campo pin vuoto", Toast.LENGTH_LONG).show();
                        }else {
                            if (!getPin.matches("[0-9.]+")) {
                                etPin.setText("");
                                Toast.makeText(MainActivity.this, "Campo pin non valido", Toast.LENGTH_LONG).show();
                            } else {
                                if (user.getPIN().equals(getPin)){
                                    //Toast.makeText(MainActivity.this, "Pin corretto", Toast.LENGTH_LONG).show();
                                    Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                                    startActivity(intentHome);
                                }
                                else {
                                    etPin.setText("");
                                    Toast.makeText(MainActivity.this, "Pin errato", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                    return false;
                }
            });
            
            etPin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Non è necessario implementare nulla in questo metodo
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Non è necessario implementare nulla in questo metodo
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String password = s.toString();
                    // Effettua il controllo sulla password
                    if (password.equals(user.getPIN())) {
                        // La password è corretta, avvia un'altra activity
                        Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                        startActivity(intentHome);
                    }
                }
            });
        }else{
            clickButton();
        }

    }

    public void clickButton(){
        bttInUp.setOnClickListener((v)->{
            Intent intentReg = new Intent(getString(R.string.LAUNCH_REGISTERACTIVITY));
            startActivity(intentReg);
        });
    }


}