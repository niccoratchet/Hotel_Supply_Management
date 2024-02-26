package com.unifisweproject.hotelsupplymanagement;

public class Item {

    private int codice_articolo, quantita;
    private double prezzo;
    private String nome, descrizione, data_inserimento;

    public Item(int quantita, double prezzo, String nome, String descrizione, String data_inserimento) {

        this.codice_articolo = -1;
        this.quantita = quantita;
        this.prezzo = prezzo;
        this.nome = nome;
        this.descrizione = descrizione;
        this.data_inserimento = data_inserimento;

    }

    public Item(int codice_articolo, int quantita, double prezzo, String nome, String descrizione, String data_inserimento) {

        this.codice_articolo = codice_articolo;
        this.quantita = quantita;
        this.prezzo = prezzo;
        this.nome = nome;
        this.descrizione = descrizione;
        this.data_inserimento = data_inserimento;

    }

    public int getCodice_articolo() {
        return codice_articolo;
    }

    public void setCodice_articolo(int codice_articolo) {
        this.codice_articolo = codice_articolo;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getData_inserimento() {
        return data_inserimento;
    }

    public void setData_inserimento(String data_inserimento) {
        this.data_inserimento = data_inserimento;
    }

}
