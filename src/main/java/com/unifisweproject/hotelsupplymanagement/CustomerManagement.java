package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CustomerManagement implements Data_Management {

    private int nextCustomerCode;               // Tiene traccia del codice dell'ultimo Cliente nel DB
    private final ArrayList<Customer> customerList = new ArrayList<>();           // Lista che contiene tutti gli Item contenuti nella tabella Cliente


    public CustomerManagement() {                                                                   // Il costruttore inizializza il contenuto della variabile nextItemCode

        String getCodeQuery = "SELECT seq FROM sqlite_sequence WHERE name = 'Cliente'";

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            nextCustomerCode = resultSet.getInt(1);
        }
        catch(SQLException e) {
            System.err.println("Errore durante l'estrapolazione dell'ultimo codice Cliente");
        }

    }

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
            nextCustomerCode++;

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    @Override
    public void modifyParameter(int code, String dataType, Object value) {

        String modifyQuery = "UPDATE Cliente SET " + getDataTypeForQuery(dataType, value) + " WHERE Codice_Cliente = " + code;
        executeQuery(modifyQuery, false);

    }

    @Override
    public void modify(Object value) {
        Customer modified = (Customer) value;
        String modifyQuery = "UPDATE Cliente SET " + getDataTypeForQuery("Data_Inserimento", modified.getData_inserimento()) + ", "
                + getDataTypeForQuery("Sconto", modified.getSconto()) + ", " + getDataTypeForQuery("Nome", modified.getNome()) + ", "
                + getDataTypeForQuery("Cognome", modified.getCognome()) + ", " + getDataTypeForQuery("Codice_Fiscale", modified.getCodice_fiscale())
                + getDataTypeForQuery("P_IVA", modified.getP_IVA()) + getDataTypeForQuery("Ragione_Sociale", modified.getRagione_sociale())
                + getDataTypeForQuery("Indirizzo", modified.getIndirizzo()) + getDataTypeForQuery("Civico", modified.getCivico())
                + getDataTypeForQuery("CAP", modified.getCAP()) + " WHERE Codice_Cliente = " + modified.getCodice_cliente();

        System.out.println(modifyQuery);
        executeQuery(modifyQuery, false);
    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Cliente WHERE " + getDataTypeForQuery(dataType, value);
        executeQuery(searchQuery, true);
        return null;

    }

    @Override
    public Object search(Object toBeSearched) {
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
            case "Codice_Cliente" -> "Codice_Cliente = " + value;
            case "Sconto" -> "Sconto = " + value;
            case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
            case "Nome" -> "Nome = '" + value + "'";
            case "Cognome" -> "Cognome = '" + value + "'";
            case "Codice_Fiscale" -> "Codice_fiscale = '" + value + "'";
            case "P_IVA" -> "P_IVA = '" + value + "'";
            case "Ragione_Sociale" -> "Ragione_Sociale = '" + value + "'";
            case "Indirizzo" -> "Indirizzo = '" + value + "'";
            case "CAP" -> "CAP = '" + value + "'";
            case "Civico" -> "Civico = '" + value + "'";
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
                            "\t" + resultSet.getString(7) + "\t" + resultSet.getString(8) +
                            "\t" + resultSet.getString(9) + "\t" + resultSet.getString(10) +
                            "\t" + resultSet.getString(11));
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

        String query = "SELECT * FROM Cliente";

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            return statement.executeQuery(query);
        }

        catch(SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;

    }

    public ArrayList<Customer> getCustomerList() {
        return customerList;
    }

    public int getNextCustomerCode() {
        return nextCustomerCode;
    }


}
