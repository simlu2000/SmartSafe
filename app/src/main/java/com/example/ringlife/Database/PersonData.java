package com.example.ringlife.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ringlife.PersonInformation.PersonInformation;

public class PersonData extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;

    private static final String DATABASE_NAME = "RingLifeDB";

    private static final String TABLE_NAME = "PersonData";

    private static final String KEY_CF = "CodiceFiscale";

    private static final String KEY_NAME = "Nome";

    private static final String KEY_SURNAME = "Cognome";

    private static final String KEY_DATEOFBIRTH = "DataDiNascita";

    private static final String KEY_TEL = "NumeroTelefono";

    private static final String KEY_SEX = "Sesso";

    private static final String KEY_GRBLOOD = "GruppoSanguigno";

    private static final String KEY_PAT = "Patologie";

    private static final String KEY_ALL = "Allergie";

    private static final String KEY_EMCON = "ContattoEmergenza";

    private static final String KEY_EMTEL = "TelefoniEmergenza";

    private static final String KEY_PIN = "Pin";



    public PersonData(Context context) {
        super(context, KEY_NAME, null, DATABASE_VERSION);
    }

    //creazione DB
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PERSON_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_CF + " STRING PRIMARY KEY,"
                + KEY_NAME + " STRING, "
                + KEY_SURNAME + " STRING, "
                + KEY_DATEOFBIRTH + " STRING, "
                + KEY_TEL + " STRING, "
                + KEY_SEX + " STRING, "
                + KEY_GRBLOOD + " STRING, "
                + KEY_PAT + " STRING, "
                + KEY_ALL + " STRING, "
                + KEY_EMCON + " STRING, "
                + KEY_EMTEL + " STRING, "
                + KEY_PIN + " STRING)";
        db.execSQL(CREATE_PERSON_TABLE);
    }

    //aggiornamento DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }



    //aggiungiamo un account
    public void addPerson(PersonInformation person){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CF, person.getCodiceFiscale());
        values.put(KEY_NAME, person.getNome());
        values.put(KEY_SURNAME, person.getCognome());
        values.put(KEY_DATEOFBIRTH, person.getDataDiNascita());
        values.put(KEY_TEL, person.getTelefono());
        values.put(KEY_SEX, person.getSesso());
        values.put(KEY_GRBLOOD, person.getGruppoSanguigno());
        values.put(KEY_PAT, person.getPatologie());
        values.put(KEY_ALL, person.getAllergie());
        values.put(KEY_EMCON, person.getContattoEmergenza());
        values.put(KEY_EMTEL, person.getTelefoniEmergenza());
        values.put(KEY_PIN, person.getPIN());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // verifichiamo l'esistenza di una persona
    public boolean ifExistPerson(){
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        if(count == 1)
            return true;
        else
            return false;

    }

    public String getPinDB(){
        String pinQuery = "SELECT " + KEY_PIN + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(pinQuery, null);
        String pinReturn = cursor.getString(10);

        cursor.close();
        db.close();
        return pinReturn;
    }

    public PersonInformation getPerson(){
        PersonInformation person;

        String personQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(personQuery, null);
        cursor.moveToFirst();
        person = new PersonInformation(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                cursor.getString(11));
        cursor.close();
        db.close();
        return person;
    }
}
