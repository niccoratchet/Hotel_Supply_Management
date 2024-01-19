package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SupplierManagement implements Data_Management{
    @Override
    public void add(Object newSupplier) {

        Supplier toBeAdded = (Supplier) newSupplier;

        String addQuery = "INSERT INTO Fornitore (Data_Inserimento, P_IVA, Ragione_Sociale, Indirizzo, CAP, Civico) \n" +       // creazione della query di inserimento
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
            preparedStatement.setString(1, toBeAdded.getData_inserimento());
            preparedStatement.setString(2, toBeAdded.getP_IVA());
            preparedStatement.setString(3, toBeAdded.getRagione_sociale());
            preparedStatement.setString(4, toBeAdded.getIndirizzo());
            preparedStatement.setString(4, toBeAdded.getCAP());
            preparedStatement.setString(4, toBeAdded.getCivico());
            preparedStatement.executeUpdate();                                                          // una volta creata, si invia il comando al DBMS

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modify(int code, String dataType, Object value) {

        String modifyQuery = "UPDATE Fornitore SET " + getDataTypeForQuery(dataType, value) + " WHERE Codice_Fornitore = " + code;
        executeQuery(modifyQuery, false);

    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Fornitore WHERE " + getDataTypeForQuery(dataType, value);
        executeQuery(searchQuery, true);
        return null;

    }

    @Override
    public void printAll() {

        String printQuery = "SELECT * FROM Fornitore";
        executeQuery(printQuery, true);

    }

    @Override
    public void print(int code) {

        String printQuery = "SELECT * FROM Fornitore WHERE Codice_Fornitore = " + code;
        executeQuery(printQuery, true);

    }

    @Override
    public void delete(int code) {

        String deleteQuery = "DELETE FROM Fornitore WHERE Codice_Fornitore = " + code;
        executeQuery(deleteQuery, false);

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value) {

        String query = switch (dataType) {
            case "Codice_Fornitore" -> "Codice_Fornitore = " + (Integer) value;
            case "Data_Inserimento" -> "Data_Inserimento = " + (String) value;
            case "P_IVA" -> "P_IVA = " + (String) value;
            case "Ragione_Sociale" -> "Ragione_Sociale = " + (String) value;
            case "Indirizzo" -> "Indirizzo = " + (String) value;
            case "CAP" -> "CAP = " + (String) value;
            case "Civico" -> "Civico = " + (String) value;
            default -> " ";
        };

        return query;

    }

    @Override
    public void executeQuery(String query, boolean isOutput) {

        try {

            Statement statement = HotelSupplyManagementMain.conn.createStatement();

            if (isOutput) {

                ResultSet resultSet = statement.executeQuery(query);

                while(resultSet.next()) {
                    System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2)
                            + "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4)
                            + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                            + "\t" + resultSet.getString(7));

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
