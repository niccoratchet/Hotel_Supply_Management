package com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply;

import java.util.ArrayList;

/**
 * Rappresenta un insieme di righe della tabella "Fornitura" del database. Ogni riga del gruppo in questione rappresenta un articolo fornito dallo stesso fornitore.
 */

public class ItemInSupply {

    private int codice_Fornitore;
    private ArrayList<Integer> codice_ArticoloList = new ArrayList<>();
    private String data_Fornitura;
    private ArrayList<Integer> quantitaList = new ArrayList<>();
    private ArrayList<Double> prezzoList = new ArrayList<>();

    public int getCodice_Fornitore() {
        return codice_Fornitore;
    }

    public void setCodice_Fornitore(int codice_Fornitore) {
        this.codice_Fornitore = codice_Fornitore;
    }

    public String getData_Fornitura() {
        return data_Fornitura;
    }

    public void setData_Fornitura(String data_Fornitura) {
        this.data_Fornitura = data_Fornitura;
    }

    public double getPrezzo(int pos) {
        return prezzoList.get(pos);
    }

    public int getQuantita(int pos) {
        return quantitaList.get(pos);
    }

    public int getCodice_Articolo(int pos) {
        return codice_ArticoloList.get(pos);
    }

    public void addPrezzo(double prezzo) {
        this.prezzoList.add(prezzo);
    }

    public void addQuantita(int quantita) {
        this.quantitaList.add(quantita);
    }

    public void addCodice_Articolo(int codice_Articolo) {
        this.codice_ArticoloList.add(codice_Articolo);
    }

    public int getNumberOfItems() {
        return codice_ArticoloList.size();
    }

}
