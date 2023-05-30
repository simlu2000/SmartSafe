package com.example.ringlife;

import static java.sql.Types.NULL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ringlife.Database.PersonData;

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

        if(!exist){
            etPin.setVisibility(View.VISIBLE);
            bttInUp.setVisibility(View.INVISIBLE);
            int dbPin = 123456;
            etPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL){
                        String getPin = etPin.getText().toString();
                        if (checkPinEmpty(getPin)) {
                            Toast.makeText(MainActivity.this, "Campo pin vuoto", Toast.LENGTH_LONG).show();
                        }else {
                            if (!checkValid(getPin)) {
                                clearEt();
                                Toast.makeText(MainActivity.this, "Campo pin non valido", Toast.LENGTH_LONG).show();
                            } else {
                                int intGetPin = Integer.parseInt(getPin);
                                if (checkPin(dbPin, intGetPin)){
                                    //Toast.makeText(MainActivity.this, "Pin corretto", Toast.LENGTH_LONG).show();
                                    Intent intentHome = new Intent(getString(R.string.LAUNCH_HOMEACTIVITY));
                                    startActivity(intentHome);
                                }
                                else {
                                    clearEt();
                                    Toast.makeText(MainActivity.this, "Pin errato", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                    return false;
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

    public boolean checkPin(int dbPin, int insPin){
        if(dbPin == insPin)
            return true;
        else
            return false;
    }

    public boolean checkPinEmpty(String insPin){
        if(insPin.matches(""))
            return true;
        else
            return false;
    }

    public boolean checkValid(String insPin){
        if(insPin.matches("[0-9.]+"))
            return true;
        else
            return false;
    }

    public void clearEt(){
        etPin.setText("");
    }


}