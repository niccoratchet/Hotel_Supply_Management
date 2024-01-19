package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CustomerManagement implements Data_Management {

    @Override
    public void add(Object newCustomer) {
        Customer toBeAdded = (Customer) newCustomer;

        String addQuery = "INSERT INTO Cliente (Sconto, Data_Inserimento, Nome, Cognome, Codice_Fiscale, P_IVA, Ragione_Sociale, Indirizzo, CAP, Civico) \n" +       // creazione della query di inserimento
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
            preparedStatement.setInt(1, toBeAdded.getSconto());
            preparedStatement.setString(2, toBeAdded.getData_inserimento());
            preparedStatement.setString(3, toBeAdded.getNome());
            preparedStatement.setString(4, toBeAdded.getCognome());
            preparedStatement.setString(5, toBeAdded.getCodice_fiscale());
            preparedStatement.setString(6, toBeAdded.getP_IVA());
            preparedStatement.setString(7, toBeAdded.getRagione_sociale());
            preparedStatement.setString(8, toBeAdded.getIndirizzo());
            preparedStatement.setString(9, toBeAdded.getCAP());
            preparedStatement.setString(10, toBeAdded.getCivico());
            preparedStatement.executeUpdate();

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    @Override
    public void modify(int code, String dataType, Object value) {

        String modifyQuery = "UPDATE Cliente SET " + getDataTypeForQuery(dataType, value) + " WHERE Codice_Cliente = " + code;
        executeQuery(modifyQuery, false);

    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Cliente WHERE " + getDataTypeForQuery(dataType, value);
        executeQuery(searchQuery, true);
        return null;

        }

    @Override
    public void printAll() {

        String printQuery = "SELECT * FROM Cliente";
        executeQuery(printQuery, true);

    }

    @Override
    public void print(int code) {

        String printQuery = "SELECT * FROM Cliente WHERE Codice_Cliente = " + code;
        executeQuery(printQuery, true);

    }

    @Override
    public void delete(int code) {

        String deleteQuery = "DELETE FROM Cliente WHERE Codice_Cliente = " + code;
        executeQuery(deleteQuery, false);

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value) {

        return switch (dataType) {
            case "Codice_Cliente" -> "Codice_Cliente = " + (Integer) value;
            case "Sconto" -> "Sconto = " + (Integer) value;
            case "Data_Inserimento" -> "Data_Inserimento = " + (String) value;
            case "Nome" -> "Nome = " + (String) value;
            case "Cognome" -> "Cognome = " + (String) value;
            case "Codice_Fiscale" -> "Codice_fiscale = " + (String) value;
            case "P_IVA" -> "P_IVA = " + (String) value;
            case "Ragione_Sociale" -> "Ragione_Sociale = " + (String) value;
            case "Indirizzo" -> "Indirizzo = " + (String) value;
            case "CAP" -> "CAP = " + (String) value;
            case "Civico" -> "Civico = " + (String) value;
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
                    System.out.println(resultSet.getInt(1) + "\t" + resultSet.getInt(2) +
                            "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4) +
                            "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6) +
                            "\t" + resultSet.getString(6) + "\t" + resultSet.getString(7) +
                            "\t" + resultSet.getString(8) + "\t" + resultSet.getString(9) +
                            "\t" + resultSet.getString(10) + "\t" + resultSet.getString(11));

                }
            }

            else
                statement.executeQuery(query);

        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }


}
