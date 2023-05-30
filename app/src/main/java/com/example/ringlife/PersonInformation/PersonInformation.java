package com.example.ringlife.PersonInformation;

import android.content.Intent;

public class PersonInformation {
    private String codiceFiscale;
    private String nome;
    private String cognome;
    private String dataDiNascita;
    private Integer telefono;
    private String sesso;
    private String gruppoSanguigno;
    private String patologie;
    private String allergie;
    private Integer PIN;

    public PersonInformation(String codiceFiscale, String nome, String cognome, String dataDiNascita,
                             Integer telefono, String sesso, String gruppoSanguigno, String patologie,
                             String allergie, Integer PIN){
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.telefono = telefono;
        this.sesso = sesso;
        this.gruppoSanguigno = gruppoSanguigno;
        this.patologie = patologie;
        this.allergie = allergie;
        this.PIN = PIN;
    }

    public String getCodiceFiscale(){ return codiceFiscale;}
    public String getNome(){return nome;}
    public String getCognome(){return cognome;}
    public String getDataDiNascita(){return dataDiNascita;}
    public Integer getTelefono(){return telefono;}
    public String getSesso(){return sesso;}
    public String getGruppoSanguigno(){return gruppoSanguigno;}
    public String getPatologie(){return patologie;}
    public String getAllergie(){return allergie;}
    public Integer getPIN(){return PIN;}
}
