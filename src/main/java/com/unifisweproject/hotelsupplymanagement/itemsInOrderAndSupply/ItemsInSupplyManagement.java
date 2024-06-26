package com.unifisweproject.hotelsupplymanagement.itemsInOrderAndSupply;

import com.unifisweproject.hotelsupplymanagement.model.item.Item;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.model.supply.Supply;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Rappresenta un insieme di righe della tabella "Fornitura" del database. Ogni riga del gruppo in questione rappresenta un articolo fornito dallo stesso fornitore.
 * */

public class ItemsInSupplyManagement {

    private final ArrayList<Double> prezziArticoli = new ArrayList<>();
    private final ArrayList<Integer> codiciArticolo = new ArrayList<>();
    private final ArrayList<Integer> quantitaPerArticolo = new ArrayList<>();


    public void updateItemAmount(ArrayList<Integer> newAmount) {       //Metodo per aggiornare la quantita di un item nel DB (usato per aggiornare la quantita di un item dopo un ordine

        for(int i = 0; i < getNumberOfItems(); i++) {
            String modifyQuery = "UPDATE Articolo SET Quantita = ? WHERE Codice_Articolo = " + codiciArticolo.get(i);       //Istruzioni per aggiornare la quantita dell'item del DB
            try {
                PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
                statement.setInt(1, newAmount.get(i));
                statement.executeUpdate();
            }
            catch (SQLException e) {
                System.err.println("Errore di formattazione nella generazione della query di modifica: " + e.getMessage());
            }
        }

    }


    public ArrayList<Item> getItemsFromASupply(Supply displayedSupply) {

        String getCodeQuery = "SELECT A.Codice_Articolo, A.Nome, A.Prezzo, A.Quantita, A.Descrizione, A.Data_Inserimento " +
                "FROM Fornitura F " +
                "RIGHT JOIN Articolo A ON F.Codice_Articolo = A.Codice_Articolo " +
                "WHERE F.Codice_Fornitura = " + displayedSupply.getCodice_fornitura();
        ArrayList<Item> items = new ArrayList<>();
        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            while (resultSet.next()) {
                int quantita = resultSet.getInt(4);
                double prezzoTot = resultSet.getDouble(3) * quantita;
                Item item = new Item(resultSet.getInt(1), quantita,
                        prezzoTot, resultSet.getString(2),
                        resultSet.getString(5), resultSet.getString(6));
                items.add(item);
            }
            return items;
        }
        catch (SQLException e) {
            System.err.println("Errore nell'esecuzione della query: " + e.getMessage());
            return null;
        }

    }

    public double getPrezzo(int pos) {
        return prezziArticoli.get(pos);
    }

    public int getQuantita(int pos) {
        return quantitaPerArticolo.get(pos);
    }

    public int getCodice_Articolo(int pos) {
        return codiciArticolo.get(pos);
    }

    public void addPrezzo(double prezzo) {
        this.prezziArticoli.add(prezzo);
    }

    public void addQuantita(int quantita) {
        this.quantitaPerArticolo.add(quantita);
    }

    public void addCodice_Articolo(int codice_Articolo) {
        this.codiciArticolo.add(codice_Articolo);
    }

    public int getNumberOfItems() {
        return codiciArticolo.size();
    }

}
