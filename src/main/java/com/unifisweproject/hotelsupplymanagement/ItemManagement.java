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

}
