package com.unifisweproject.hotelsupplymanagement.model.supply;
public class Supply {               // Rappresenta una singola riga della tabella "Fornitura" del database

    private int codice_fornitura;
    private int codice_fornitore;
    private int codice_articolo;
    private String data_fornitura;
    private int quantita;
    private double prezzo;                     // Prezzo di acquisto dal fornitore (NON al dettaglio)

    public Supply(int codice_fornitore, int codice_articolo, String data_fornitura, int quantita, double prezzo) {

        this.codice_fornitura = -1;
        this.codice_fornitore = codice_fornitore;
        this.codice_articolo = codice_articolo;
        this.data_fornitura = data_fornitura;
        this.quantita = quantita;
        this.prezzo = prezzo;

    }

    public Supply(int codice_fornitura, int codice_fornitore, int codice_articolo, String data_fornitura, int quantita, double prezzo) {

        this.codice_fornitura = codice_fornitura;
        this.codice_fornitore = codice_fornitore;
        this.codice_articolo = codice_articolo;
        this.data_fornitura = data_fornitura;
        this.quantita = quantita;
        this.prezzo = prezzo;

    }

    public int getCodice_fornitore() {
        return codice_fornitore;
    }

    public int getCodice_articolo() {
        return codice_articolo;
    }

    public String getData_fornitura() {
        return data_fornitura;
    }

    public int getQuantita() {
        return quantita;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setCodice_fornitore(int codice_fornitore) {
        this.codice_fornitore = codice_fornitore;
    }

    public void setCodice_articolo(int codice_articolo) {
        this.codice_articolo = codice_articolo;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getCodice_fornitura() {
        return codice_fornitura;
    }

    public void setCodice_fornitura(int codice_fornitura) {
        this.codice_fornitura = codice_fornitura;
    }
    public void setData_fornitura(String data_fornitura) {
        this.data_fornitura = data_fornitura;
    }

}
