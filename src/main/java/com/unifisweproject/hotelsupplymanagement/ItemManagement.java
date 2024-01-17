package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ItemManagement implements Data_Management {

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

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void modify(int code, String dataType, Object value) {

         String modifyQuery = "UPDATE Articolo SET ";

         modifyQuery += getDataTypeForQuery(dataType, value);

         modifyQuery += "WHERE Codice_Articolo = " + code;

         try {
             Statement statement = HotelSupplyManagementMain.conn.createStatement();
             statement.executeQuery(modifyQuery);
         }
         catch(SQLException e) {
             System.err.println(e.getMessage());
         }

    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Articolo WHERE ";

        searchQuery += getDataTypeForQuery(dataType, value);

        try {

            Statement statement = HotelSupplyManagementMain.conn.createStatement();

            ResultSet resultSet = statement.executeQuery(searchQuery);

            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2) +
                        "\t" + resultSet.getDouble(3) + "\t" + resultSet.getInt(4) + "\t" +
                        resultSet.getString(5) + "\t" + resultSet.getString(6));

            }

        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;

    }

    @Override
    public void printAll() {

        String printQuery = "SELECT * FROM Articolo";

        try {

            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(printQuery);

            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2) +
                        "\t" + resultSet.getDouble(3) + "\t" + resultSet.getInt(4) + "\t" +
                        resultSet.getString(5) + "\t" + resultSet.getString(6));

            }

        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    @Override
    public void print(Object tobeViewed) {

    }

    @Override
    public void delete(int code) {

        String deleteQuery = "DELETE FROM Articolo WHERE Codice_Articolo = " + code;

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            statement.executeQuery(deleteQuery);
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public String getDataTypeForQuery(String dataType, Object value) {

        String query = " ";

        switch (dataType) {

            case "Codice_Articolo":
                query = "Codice_Articolo = " + (Integer) value;
                break;
            case "Prezzo":
                query = "Prezzo = " + (Double) value;
                break;
            case "Quantita":
                query = "Quantita = " + (Integer) value;
                break;
            case "Nome":
                query = "Nome = " + (String) value;
                break;
            case "Descrizione":
                query = "Descrizione = " + (String) value;
                break;
            case "Data_Inserimento":
                query = "Data_Inserimento = " + (String) value;
                break;

        }

        return query;

    }

}
