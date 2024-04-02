package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SuppliesManagement implements Data_Management {

    /*
        Questa classe è stata creata per gestire la tabella "Fornitura" del database. Non ha bisogno dell'attributo nextItemCode per il fatto
        che non è possibile aggiungere nuovi elementi alla tabella se non sono già presenti Fornitori e Articoli.
     */
    private int nextSupplyCode;                 // Codice della prossima fornitura da aggiungere
    private final ArrayList<Supply> suppliesList = new ArrayList<>();               // Lista di oggetti che rappresenta una singola riga in Fornitura nel DB

    public SuppliesManagement() {

        String getCodeQuery = "SELECT seq FROM sqlite_sequence WHERE name = 'Fornitura'";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(getCodeQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            nextSupplyCode = resultSet.getInt(1);
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione del prossimo codice fornitura: " + e.getMessage());
        }

    }

    @Override
    public void add(Object newObject) {

        Supply toBeAdded = (Supply) newObject;
        String addQuery = "INSERT INTO Fornitura (Codice_Articolo, Codice_Fornitore, Data_Fornitura, Prezzo, Quantita) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
            preparedStatement.setInt(1, toBeAdded.getCodice_articolo());
            preparedStatement.setInt(2, toBeAdded.getCodice_fornitore());
            preparedStatement.setString(3, toBeAdded.getData_fornitura());
            preparedStatement.setDouble(4, toBeAdded.getPrezzo());
            preparedStatement.setInt(5, toBeAdded.getQuantita());
            preparedStatement.executeUpdate();
            nextSupplyCode++;
        }
        catch (Exception e) {
            System.out.println("Errore durante l'aggiunta di una nuova fornitura: " + e.getMessage());
        }

    }

    @Override
    public void modify(Object value) {

        /*  Si è deciso di non implementare la modifica

        Supply modified = (Supply) value;
        String modifyQuery = "UPDATE Fornitura SET " + getDataTypeForQuery("Codice_Articolo", modified.getCodice_articolo(), false) + ", " +
                getDataTypeForQuery("Codice_Fornitore", modified.getCodice_fornitore(), false) + ", " + getDataTypeForQuery("Data_Fornitura", modified.getData_fornitura(), false) + ", " +
                getDataTypeForQuery("Prezzo", modified.getPrezzo(), false) + ", " + getDataTypeForQuery("Quantita", modified.getQuantita(), false) +
                " WHERE Codice_Fornitura = " +getDataTypeForQuery("Codice_Fornitura", modified.getCodice_fornitura(), true
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            executeQuery(false, preparedStatement);
        }
        catch (Exception e) {
            System.out.println("Errore durante la modifica di una fornitura: " + e.getMessage());
        }

         */

    }

    @Override
    public ArrayList<Object> search(Object toBeSearched) {

        Supply supply = (Supply) toBeSearched;
        int numberOfParameters = getNumberOfParameters(supply);
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Fornitura WHERE ");
        int i = 0;
        while (i < 6 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(supply.getCodice_fornitura() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Fornitura", supply.getCodice_fornitura(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 1 -> {
                    if(supply.getCodice_articolo() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Articolo", supply.getCodice_articolo(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(supply.getCodice_fornitore() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Fornitore", supply.getCodice_fornitore(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 3 -> {
                    if(supply.getData_fornitura() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_Fornitura", supply.getData_fornitura(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 4 -> {
                    if(supply.getPrezzo() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Prezzo", supply.getPrezzo(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 5 -> {
                    if(supply.getQuantita() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Quantita", supply.getQuantita(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
            }
            i++;
        }
        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(searchQuery.toString());
            return getSearchResults(getRows(false, statement));
        }
        catch (SQLException e) {
            System.err.println("Query di ricerca del cliente non correttamente formattata: " + e.getMessage());
            return null;
        }

    }

    public ArrayList<Object> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Item che corrispondono alle righe indicate

        ArrayList<Object> results = new ArrayList<>();                // conterrà gli Item che corrispondono ai valori trovati dopo la query

        try {
            while (resultSet.next()) {
                for (Supply nextItem : suppliesList) {
                    if (nextItem.getCodice_fornitura() == resultSet.getInt(1)) {
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

    public int getNumberOfParameters(Supply forCounting) {

        int i = 0, count = 0;
        while (i < 6) {

            switch (i) {
                case 0 -> {
                    if (forCounting.getCodice_articolo() != -1)
                        count++;
                }
                case 1 -> {
                    if (forCounting.getCodice_fornitore() != -1)
                        count++;
                }
                case 2 -> {
                    if (forCounting.getData_fornitura() != null)
                        count++;
                }
                case 3 -> {
                    if (forCounting.getPrezzo() != -1)
                        count++;
                }
                case 4 -> {
                    if (forCounting.getQuantita() != -1)
                        count++;
                }
                case 5 -> {
                    if (forCounting.getCodice_fornitura() != -1)
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
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM Fornitura WHERE Codice_Fornitura = " + code);
            executeQuery(false, statement);
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della riga Customer: " + e.getMessage());
        }

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value, boolean isSelect) {

        return switch (dataType) {
            case "Codice_Fornitura" -> "Codice_Fornitura" + value;
            case "Codice_Articolo" -> "Codice_Articolo = " + value;
            case "Codice_Fornitore" -> "Codice_Fornitore = " + value;
            case "Data_Fornitura" -> "Data_Fornitura = '" + value + "'";
            case "Prezzo" -> "Prezzo = " + value;
            case "Quantita" -> "Quantita = " + value;
            default -> " ";
        };

    }


    @Override
    public void executeQuery(boolean isOutput, PreparedStatement statement) {
        try {
            if (! isOutput)
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
                toBeExecutedQuery = "SELECT * FROM Fornitura ORDER BY Data_Fornitura DESC";
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
    public ArrayList<Supply> getSuppliesList() {
        return suppliesList;
    }

    public int getNextSupplyCode() {
        return nextSupplyCode;
    }

}