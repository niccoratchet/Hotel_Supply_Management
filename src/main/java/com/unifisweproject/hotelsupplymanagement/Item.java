package com.unifisweproject.hotelsupplymanagement;

public class Item {

    private int codice_articolo, quantità;
    private double prezzo;
    private String nome, descrizione, data_inserimento;

    public Item(int codice_articolo, int quantità, double prezzo, String nome, String descrizione, String data_inserimento) {

        this.codice_articolo = codice_articolo;
        this.quantità = quantità;
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

    public int getQuantità() {
        return quantità;
    }

    public void setQuantità(int quantità) {
        this.quantità = quantità;
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
