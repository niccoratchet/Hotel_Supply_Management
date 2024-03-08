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
            System.out.println("Errore durante l'aggiunta del nuovo Cliente: " +e.getMessage() + " \n Query utilizzata: " + addQuery);
        }

    }

    @Override
    public void modify(Object value) {

        Customer modified = (Customer) value;
        String modifyQuery = "UPDATE Cliente SET " + getDataTypeForQuery("Data_Inserimento", modified.getData_inserimento(), false) + ", "
                + getDataTypeForQuery("Sconto", modified.getSconto(), false) + ", " + getDataTypeForQuery("Nome", modified.getNome(), false) + ", "
                + getDataTypeForQuery("Cognome", modified.getCognome(), false) + ", " + getDataTypeForQuery("Codice_Fiscale", modified.getCodice_fiscale(), false)
                + getDataTypeForQuery("P_IVA", modified.getP_IVA(), false) + getDataTypeForQuery("Ragione_Sociale", modified.getRagione_sociale(), false)
                + getDataTypeForQuery("Indirizzo", modified.getIndirizzo(), false) + getDataTypeForQuery("Civico", modified.getCivico(), false)
                + getDataTypeForQuery("CAP", modified.getCAP(), false) + " WHERE Codice_Cliente = " + modified.getCodice_cliente();

        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            statement.setString(1, modified.getNome());
            statement.setString(2, modified.getCognome());
            statement.setString(3, modified.getRagione_sociale());
            statement.setString(4, modified.getIndirizzo());
            executeQuery(false, statement);
        }
        catch (SQLException e) {
            System.err.println("Errore di formattazione nella generazione della query di modifica: " + e.getMessage());
        }

    }

    @Override
    public ArrayList<Object> search(Object toBeSearched) {

        Customer customer = (Customer) toBeSearched;
        int numberOfParameters = getNumberOfParameters(customer), numQuestionMarks = 0;
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Cliente WHERE ");
        boolean isNamePresent = false, isSurnamePresent = false, isRagioneSocialePresent = false, isIndirizzoPresent = false;
        int i = 0;
        while (i < 10 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(customer.getData_inserimento() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_Inserimento", customer.getData_inserimento(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 1 -> {
                    if(customer.getSconto() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Sconto", customer.getSconto(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(customer.getNome() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Nome", customer.getNome(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isNamePresent = true;
                        numQuestionMarks++;
                    }
                }
                case 3 -> {
                    if(customer.getCognome() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Cognome ", customer.getCognome(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isSurnamePresent = true;
                        numQuestionMarks++;
                    }
                }

                case 4 -> {
                    if(customer.getCodice_fiscale() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Descrizione", customer.getCodice_fiscale(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }

                case 5 -> {
                    if(customer.getP_IVA() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("P_IVA", customer.getP_IVA(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }

                case 6 -> {
                    if(customer.getRagione_sociale() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Ragione_Sociale", customer.getRagione_sociale(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isRagioneSocialePresent = true;
                        numQuestionMarks++;
                    }
                }

                case 7 -> {
                    if(customer.getIndirizzo() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Indirizzo", customer.getIndirizzo(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isIndirizzoPresent = true;
                        numQuestionMarks++;
                    }
                }

                case 8 -> {
                    if(customer.getCivico() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Civico", customer.getCivico(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 9 -> {
                    if(customer.getCAP() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("CAP", customer.getCAP(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }

            }
            i++;
        }
        try {

            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(searchQuery.toString());

            i = 0;
            int parameterIndex = 1;
            while (i < 4 && numQuestionMarks > 0) {

                switch (i) {                                                                    // Questo switch serve per capire quali parametri e quanti devono avere il carattere ? sostituito
                    case 0 -> {
                        if (isNamePresent) {
                            String nameValue = "%" + customer.getNome() + "%";
                            statement.setString(parameterIndex, nameValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                    case 1 -> {
                        if (isSurnamePresent) {
                            String surnameValue = "%" + customer.getCognome() + "%";
                            statement.setString(parameterIndex, surnameValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                    case 2 -> {
                        if(isRagioneSocialePresent) {
                            String ragioneSocialeValue = "%" + customer.getRagione_sociale() + "%";
                            statement.setString(parameterIndex, ragioneSocialeValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                    case 3 -> {
                        if(isIndirizzoPresent) {
                            String indirizzoValue = "%" + customer.getIndirizzo() + "%";
                            statement.setString(parameterIndex, indirizzoValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                }
                i++;
            }
            return getSearchResults(getRows(false, statement));
        }
        catch (SQLException e) {
            System.err.println("Query di ricerca non correttamente formattata");
            return null;
        }

    }

    public ArrayList<Object> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Item che corrispondono alle righe indicate

        ArrayList<Object> results = new ArrayList<>();                // conterrà gli Item che corrispondono ai valori trovati dopo la query

        try {
            while (resultSet.next()) {
                for (Customer nextItem : customerList) {
                    if (nextItem.getCodice_cliente() == resultSet.getInt(1)) {
                        results.add(nextItem);
                    }
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione del risultato di ricerca: " + e.getMessage());
            return null;
        }
        return results;

    }

    public int getNumberOfParameters(Customer forCounting) {

        int i = 0, count = 0;
        while (i < 10) {

            switch (i) {
                case 0 -> {
                    if (forCounting.getData_inserimento() != null)
                        count++;
                }
                case 1 -> {
                    if (forCounting.getSconto() != -1)
                        count++;
                }
                case 2 -> {
                    if (forCounting.getNome() != null)
                        count++;
                }
                case 3 -> {
                    if (forCounting.getCognome() != null)
                        count++;
                }
                case 4 -> {
                    if (forCounting.getCodice_fiscale() != null)
                        count++;
                }
                case 5 -> {
                    if (forCounting.getP_IVA() != null)
                        count++;
                }
                case 6 -> {
                    if (forCounting.getRagione_sociale() != null)
                        count++;
                }
                case 7 -> {
                    if (forCounting.getIndirizzo() != null)
                        count++;
                }
                case 8 -> {
                    if (forCounting.getCivico() != null)
                        count++;
                }
                case 9 -> {
                    if (forCounting.getCAP() != null)
                        count++;
                }
            }
            i++;
        }
        return count;

    }


    @Override
    public void delete(int code) {

        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM Cliente WHERE Codice_Cliente = " + code);
            executeQuery(false, statement);
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della riga Customer: " + e.getMessage());
        }


    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value, boolean isSelect) {

        if (isSelect) {
            return switch (dataType) {
                case "Codice_Cliente" -> "Codice_Cliente = " + value;
                case "Sconto" -> "Sconto = " + value;
                case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
                case "Nome" -> "Nome LIKE ?";
                case "Cognome" -> "Cognome LIKE ?";
                case "Codice_Fiscale" -> "Codice_fiscale = '" + value + "'";            // TODO: Applicare il formatter corretto per il codice fiscale
                case "P_IVA" -> "P_IVA = '" + value + "'";                              // TODO: Applicare il formatter corretto per la P_IVA
                case "Ragione_Sociale" -> "Ragione_Sociale LIKE ?";
                case "Indirizzo" -> "Indirizzo LIKE ?";
                case "CAP" -> "CAP = '" + value + "'";                                  // TODO: Applicare il formatter corretto per il CAP
                case "Civico" -> "Civico = '" + value + "'";                            // TODO: Applicare il formatter corretto per il Civico
                default -> " ";
            };
        }
        else {
            return switch (dataType) {
                case "Codice_Cliente" -> "Codice_Cliente = " + value;
                case "Sconto" -> "Sconto = " + value;
                case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
                case "Nome" -> "Nome = ?";
                case "Cognome" -> "Cognome = ?";
                case "Codice_Fiscale" -> "Codice_fiscale = '" + value + "'";
                case "P_IVA" -> "P_IVA = '" + value + "'";
                case "Ragione_Sociale" -> "Ragione_Sociale = ?";
                case "Indirizzo" -> "Indirizzo = ?";
                case "CAP" -> "CAP = '" + value + "'";
                case "Civico" -> "Civico = '" + value + "'";
                default -> " ";
            };
        }

    }

    @Override
    public void executeQuery(boolean isOutput, PreparedStatement statement) {

        try {
            if (isOutput) {
                ResultSet resultSet = statement.executeQuery();
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
                statement.executeUpdate();
        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public ResultSet getRows(boolean areAllRowsRequested, PreparedStatement statement) {

        String toBeExecutedQuery;
        try {
            if (areAllRowsRequested) {
                toBeExecutedQuery = "SELECT * FROM Cliente";
                PreparedStatement allRowsQuery = HotelSupplyManagementMain.conn.prepareStatement(toBeExecutedQuery);
                return allRowsQuery.executeQuery();
            }
            return statement.executeQuery();
        }
        catch (SQLException e) {
            System.err.println("Errore durante il reperimento delle righe dalla tabella Cliente");
            return null;
        }

    }

    public ArrayList<Customer> getCustomerList() {
        return customerList;
    }

    public int getNextCustomerCode() {
        return nextCustomerCode;
    }


}
