package com.unifisweproject.hotelsupplymanagement.supply;

import com.unifisweproject.hotelsupplymanagement.data.Data_Management;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SuppliesManagement implements Data_Management {

    /*
        Questa classe è stata creata per gestire la tabella "Fornitura" del database. Non ha bisogno dell'attributo nextItemCode per il fatto
        che non è possibile aggiungere nuovi elementi alla tabella se non sono già presenti Fornitori e Articoli.
     */

    private static final SuppliesManagement instance = new SuppliesManagement();        // Singleton per SuppliesManagement
    private int nextSupplyCode;                 // Codice della prossima fornitura da aggiungere
    private final ArrayList<Supply> suppliesList = new ArrayList<>();               // Lista di oggetti che rappresenta una singola riga in Fornitura nel DB

    private SuppliesManagement() {              // Costruttore privato per evitare la creazione di nuove istanze (Singleton) e per estrapolare il prossimo codice fornitura

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

    public static SuppliesManagement getInstance() {
        return instance;
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
        // Non prevista la modifica di una fornitura
    }

    @Override
    public ArrayList<Object> search(Object toBeSearched) {

        Supply supply = (Supply) toBeSearched;
        int numberOfParameters = getNumberOfParameters(supply);
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Fornitura WHERE ");
        int i = 0;
        while (i < 3 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(supply.getCodice_articolo() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Articolo", supply.getCodice_articolo(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 1 -> {
                    if(supply.getCodice_fornitore() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Fornitore", supply.getCodice_fornitore(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(supply.getData_fornitura() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_Fornitura", supply.getData_fornitura(), true));
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
                for (Supply nextSupply : suppliesList) {
                    if (nextSupply.getCodice_fornitura() == resultSet.getInt(1)) {
                        results.add(nextSupply);
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
        while (i < 3) {
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
            if (!isOutput)
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
