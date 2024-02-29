package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ItemManagement implements Data_Management {

    private int nextItemCode;               // Tiene traccia del codice dell'ultimo Articolo nel DB
    private final ArrayList<Item> itemList = new ArrayList<>();           // Lista che contiene tutti gli Item contenuti nella tabella Articolo

    public ItemManagement() {

        String getCodeQuery = "SELECT seq FROM sqlite_sequence WHERE name = 'Articolo'";

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            nextItemCode = resultSet.getInt(1);
        }
        catch(SQLException e) {
            System.err.println("Errore durante l'estrapolazione dell'ultimo codice articolo");
        }

    }

    @Override
    public void add(Object newItem) {

        Item toBeAdded = (Item) newItem;

        String addQuery = "INSERT INTO Articolo (Nome, Prezzo, Quantita, Descrizione, Data_Inserimento) \n" +       // creazione della query di inserimento
                    "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
            preparedStatement.setString(1, toBeAdded.getNome());
            preparedStatement.setDouble(2, toBeAdded.getPrezzo());
            preparedStatement.setInt(3, toBeAdded.getQuantita());
            preparedStatement.setString(4, toBeAdded.getDescrizione());
            preparedStatement.setString(5, toBeAdded.getData_inserimento());
            preparedStatement.executeUpdate();                                                          // una volta creata, si invia il comando al DBMS
            nextItemCode++;

        }

        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void modify(int code, String dataType, Object value) {

         String modifyQuery = "UPDATE Articolo SET " + getDataTypeForQuery(dataType, value) + " WHERE Codice_Articolo = " + code;
         executeQuery(modifyQuery, false);

    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Articolo WHERE " + getDataTypeForQuery(dataType, value);
        executeQuery(searchQuery, true);
        return null;

    }

    @Override
    public void printAll() {

        String printQuery = "SELECT * FROM Articolo";
        executeQuery(printQuery, true);

    }

    @Override
    public void print(int code) {

        String printQuery = "SELECT * FROM Articolo WHERE Codice_Articolo = " + code;
        executeQuery(printQuery, true);

    }

    @Override
    public void delete(int code) {

        String deleteQuery = "DELETE FROM Articolo WHERE Codice_Articolo = " + code;
        executeQuery(deleteQuery, false);

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value) {

        return switch (dataType) {
            case "Codice_Articolo" -> "Codice_Articolo = " + value;
            case "Prezzo" -> "Prezzo = " + value;
            case "Quantita" -> "Quantita = " + value;
            case "Nome" -> "Nome = " + value;
            case "Descrizione" -> "Descrizione = " + value;
            case "Data_Inserimento" -> "Data_Inserimento = " + value;
            default -> " ";
        };

    }

    @Override
    public void executeQuery(String query, boolean isOutput) {

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();

            if (isOutput) {
                ResultSet resultSet = statement.executeQuery(query);

                while(resultSet.next()) {
                    System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2) +
                            "\t" + resultSet.getDouble(3) + "\t" + resultSet.getInt(4) + "\t" +
                            resultSet.getString(5) + "\t" + resultSet.getString(6));
                }
            }

            else
                statement.executeUpdate(query);
        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public ResultSet getRows() {

        String query = "SELECT * FROM Articolo";

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            return statement.executeQuery(query);
        }

        catch(SQLException e) {
                System.err.println(e.getMessage());
        }

        return null;

    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public int getNextItemCode() {
        return nextItemCode;
    }
}

