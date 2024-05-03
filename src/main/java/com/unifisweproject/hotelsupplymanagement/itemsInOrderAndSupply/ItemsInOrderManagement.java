package com.unifisweproject.hotelsupplymanagement.itemsInOrderAndSupply;

import com.unifisweproject.hotelsupplymanagement.model.item.Item;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.model.order.Order;

import java.sql.*;
import java.util.ArrayList;

/**
 * Questa classe gestisce la tabella del DB ArticoloInOrdine e aiuta OrderManagementModel a gestire gli item in un ordine
 */

public class ItemsInOrderManagement {

    private int codice_Ordine;
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

    public void insertItemInOrderRow() {

        for(int i = 0; i < getNumberOfItems(); i++) {
            String addQuery = "INSERT INTO ArticoloInOrdine (Codice_Ordine, Codice_Articolo, Quantita) \n" +       // creazione della query di inserimento
                    "VALUES (?, ?, ?)";
            try {
                PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
                preparedStatement.setInt(1, codice_Ordine);
                preparedStatement.setInt(2, codiciArticolo.get(i));
                preparedStatement.setInt(3, quantitaPerArticolo.get(i));
                preparedStatement.executeUpdate();                                                          // una volta creata, si invia il comando al DBMS
            }
            catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta di un item in un order: "+ e.getMessage() +" \n Query utilizzata: " + addQuery);
            }
        }

    }

    public static ArrayList<Item> getItemFromAnOrder(Order displayedOrder) {

        String getCodeQuery = "SELECT A.Codice_Articolo, A.Nome, A.Prezzo, AIO.Quantita, A.Descrizione, A.Data_Inserimento " +
                "FROM ArticoloInOrdine AIO " +
                "RIGHT JOIN Articolo A ON AIO.Codice_Articolo = A.Codice_Articolo " +
                "WHERE AIO.Codice_Ordine = " + displayedOrder.getCodice_ordine();
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
            System.err.println("Errore nell'esecuzione della query");
            return null;
        }

    }

    public static void deleteItemInOrderRows(Object toBeDeleted) {

        try {
            PreparedStatement itemInOrderStatement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM ArticoloInOrdine WHERE Codice_Ordine = " + ((Order) toBeDeleted).getCodice_ordine());
            itemInOrderStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println("Errore nell'esecuzione della query" + e.getMessage());
        }

    }

    public void insertItemInOrderRows(ArrayList<Integer> amounts, int orderCode) {

        updateItemAmount(amounts);
        setCodice_Ordine(orderCode);
        insertItemInOrderRow();

    }

    public void setCodice_Ordine(int codice_Ordine){
        this.codice_Ordine = codice_Ordine;
    }

    public void addCodice_Articolo(int codice_Articolo){
        this.codiciArticolo.add(codice_Articolo);
    }

    public void addQuantita(int quantita){
        this.quantitaPerArticolo.add(quantita);
    }

    public int getNumberOfItems(){
        return codiciArticolo.size();
    }


}
