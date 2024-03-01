package com.unifisweproject.hotelsupplymanagement;

public class Customer {

    private int codice_cliente, sconto;
    private String nome, cognome, data_inserimento, codice_fiscale, P_IVA, ragione_sociale, indirizzo, CAP, civico;

    public Customer(int sconto, String nome, String cognome, String data_inserimento, String codice_fiscale,
                    String p_IVA, String ragione_sociale, String indirizzo, String CAP, String civico) {

        this.codice_cliente = -1;
        this.sconto = sconto;
        this.nome = nome;
        this.cognome = cognome;
        this.data_inserimento = data_inserimento;
        this.codice_fiscale = codice_fiscale;
        this.P_IVA = p_IVA;
        this.ragione_sociale = ragione_sociale;
        this.indirizzo = indirizzo;
        this.CAP = CAP;
        this.civico = civico;

    }

    public  Customer(int codice_cliente, int sconto, String nome, String cognome, String data_inserimento, String codice_fiscale,
                     String p_IVA, String ragione_sociale, String indirizzo, String CAP, String civico) {

        this.codice_cliente = codice_cliente;
        this.sconto = sconto;
        this.nome = nome;
        this.cognome = cognome;
        this.data_inserimento = data_inserimento;
        this.codice_fiscale = codice_fiscale;
        this.P_IVA = p_IVA;
        this.ragione_sociale = ragione_sociale;
        this.indirizzo = indirizzo;
        this.CAP = CAP;
        this.civico = civico;
    }

    public int getCodice_cliente() {
        return codice_cliente;
    }

    public void setCodice_cliente(int codice_cliente) {
        this.codice_cliente = codice_cliente;
    }

    public int getSconto() {
        return sconto;
    }

    public void setSconto(int sconto) {
        this.sconto = sconto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getData_inserimento() {
        return data_inserimento;
    }

    public void setData_inserimento(String data_inserimento) {
        this.data_inserimento = data_inserimento;
    }

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
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
