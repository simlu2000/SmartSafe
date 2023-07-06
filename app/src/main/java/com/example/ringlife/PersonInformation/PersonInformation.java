package com.example.ringlife.PersonInformation;


public class PersonInformation {
    private String codiceFiscale;
    private String nome;
    private String cognome;
    private String dataDiNascita;
    private String telefono;
    private String sesso;
    private String gruppoSanguigno;
    private String patologie;
    private String allergie;
    private String contattoEmergenza;
    private String telefoniEmergenza;
    private String PIN;

    // Costruttore utente
    public PersonInformation(String codiceFiscale, String nome, String cognome, String dataDiNascita,
                             String telefono, String sesso, String gruppoSanguigno, String patologie,
                             String allergie, String contattoEmergenza, String telefoniEmergenza, String PIN){
        this.codiceFiscale = codiceFiscale;
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.telefono = telefono;
        this.sesso = sesso;
        this.gruppoSanguigno = gruppoSanguigno;
        this.patologie = patologie;
        this.allergie = allergie;
        this.contattoEmergenza = contattoEmergenza;
        this.telefoniEmergenza = telefoniEmergenza;
        this.PIN = PIN;
    }

    // Metodi getter e setter
    public String getCodiceFiscale(){ return codiceFiscale;}
    public String getNome(){return nome;}
    public String getCognome(){return cognome;}
    public String getDataDiNascita(){return dataDiNascita;}
    public String getTelefono(){return telefono;}
    public String getSesso(){return sesso;}
    public String getGruppoSanguigno(){return gruppoSanguigno;}
    public String getPatologie(){return patologie;}
    public String getAllergie(){return allergie;}
    public String getContattoEmergenza(){return contattoEmergenza;}
    public String getTelefoniEmergenza(){return telefoniEmergenza;}
    public String getPIN(){return PIN;}
}
