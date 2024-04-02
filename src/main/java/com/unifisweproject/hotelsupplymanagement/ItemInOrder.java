package com.unifisweproject.hotelsupplymanagement;

import java.util.ArrayList;

public class ItemInOrder {
    private int codice_Ordine;
    private ArrayList<Integer> codice_Articolo = new ArrayList<>();
    private ArrayList<Integer> quantita = new ArrayList<>();

    public void setCodice_Ordine(int codice_Ordine){
        this.codice_Ordine = codice_Ordine;
    }

    public int getCodice_Ordine(){
        return codice_Ordine;
    }

    public int getCodice_Articolo(int pos){
        return codice_Articolo.get(pos);
    }

    public int getQuantita(int pos){
        return quantita.get(pos);
    }

    public void addCodice_Articolo(int codice_Articolo){
        this.codice_Articolo.add(codice_Articolo);
    }

    public void addQuantita(int quantita){
        this.quantita.add(quantita);
    }

    public int getNumberOfItems(){
        return codice_Articolo.size();
    }


}
