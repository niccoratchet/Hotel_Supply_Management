package com.unifisweproject.hotelsupplymanagement.supplier;

public class Supplier {

    private int codice_fornitore;
    private String data_inserimento, P_IVA, ragione_sociale, indirizzo, CAP, civico;

    public Supplier(String data_inserimento, String p_IVA, String ragione_sociale, String indirizzo, String CAP, String civico) {

        this.codice_fornitore = -1;
        this.data_inserimento = data_inserimento;
        this.P_IVA = p_IVA;
        this.ragione_sociale = ragione_sociale;
        this.indirizzo = indirizzo;
        this.CAP = CAP;
        this.civico = civico;

    }
    public Supplier(int codice_fornitore, String data_inserimento, String p_IVA, String ragione_sociale, String indirizzo, String CAP, String civico) {

        this.codice_fornitore = codice_fornitore;
        this.data_inserimento = data_inserimento;
        this.P_IVA = p_IVA;
        this.ragione_sociale = ragione_sociale;
        this.indirizzo = indirizzo;
        this.CAP = CAP;
        this.civico = civico;

    }

    public int getCodice_fornitore() {
        return codice_fornitore;
    }

    public void setCodice_fornitore(int codice_fornitore) {
        this.codice_fornitore = codice_fornitore;
    }

    public String getData_inserimento() {
        return data_inserimento;
    }

    public void setData_inserimento(String data_inserimento) {
        this.data_inserimento = data_inserimento;
    }

    public String getP_IVA() {
        return P_IVA;
    }

    public void setP_IVA(String p_IVA) {
        P_IVA = p_IVA;
    }

    public String getRagione_sociale() {
        return ragione_sociale;
    }

    public void setRagione_sociale(String ragione_sociale) {
        this.ragione_sociale = ragione_sociale;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getCAP() {
        return CAP;
    }

    public void setCAP(String CAP) {
        this.CAP = CAP;
    }

    public String getCivico() {
        return civico;
    }

    public void setCivico(String civico) {
        this.civico = civico;
    }

}
