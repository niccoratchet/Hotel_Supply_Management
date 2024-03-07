package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ItemManagement implements Data_Management {
    private int nextItemCode;               // Tiene traccia del codice dell'ultimo Articolo nel DB
    private final ArrayList<Item> itemList = new ArrayList<>();           // Lista che contiene tutti gli Item contenuti nella tabella Articolo

    public ItemManagement() {                                                                   // Il costruttore inizializza il contenuto della variabile nextItemCode

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
    public void modifyParameter(int code, String dataType, Object value) {

         String modifyQuery = "UPDATE Articolo SET " + getDataTypeForQuery(dataType, value) + " WHERE Codice_Articolo = " + code;
         executeQuery(modifyQuery, false);

    }

    @Override
    public void modify(Object value) {

        Item modified = (Item) value;
        String modifyQuery = "UPDATE Articolo SET " + getDataTypeForQuery("Nome", modified.getNome()) + ", "
                + getDataTypeForQuery("Prezzo", modified.getPrezzo()) + ", " + getDataTypeForQuery("Quantita", modified.getQuantita()) + ", "
                + getDataTypeForQuery("Descrizione", modified.getDescrizione()) + ", " + getDataTypeForQuery("Data_Inserimento", modified.getData_inserimento()) +
                " WHERE Codice_Articolo = " + modified.getCodice_articolo();
        executeQuery(modifyQuery, false);

    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Articolo WHERE " + getDataTypeForQuery(dataType, value);
        executeQuery(searchQuery, true);
        return null;

    }


    public ArrayList<Item> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Item che corrispondono alle righe indicate

        ArrayList<Item> results = new ArrayList<>();                // conterrà gli Item che corrispondono ai valori trovati dopo la query

        try {
            while (resultSet.next()) {
                for (Item nextItem : itemList) {
                    if (nextItem.getCodice_articolo() == resultSet.getInt(1)) {
                        results.add(nextItem);
                    }
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione del risultato di ricerca");
            return null;
        }
        return results;

    }

    @Override
    public ArrayList<Item> search(Object toBeSearched) {

        Item item = (Item) toBeSearched;
        int numberOfParameters = getNumberOfParameters(item);
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Articolo WHERE ");

        int i = 0;
        while (i < 5 && numberOfParameters > 0) {

            switch (i) {
                case 0 -> {

                    if(item.getNome() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Nome", item.getNome()));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }

                }
                case 1 -> {

                    if(item.getPrezzo() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Prezzo", item.getPrezzo()));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }

                }
                case 2 -> {

                    if(item.getQuantita() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Quantita", item.getQuantita()));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }

                }
                case 3 -> {

                    if(item.getData_inserimento() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_Inserimento", item.getData_inserimento()));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }

                }

                case 4 -> {

                    if(item.getDescrizione() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Descrizione", item.getDescrizione()));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }

                }
            }

            i++;

        }

        return getSearchResults(getRows(false, searchQuery.toString()));

    }

    public int getNumberOfParameters(Item forCounting) {

        int i = 0, count = 0;
        while (i < 5) {

            switch (i) {
                case 0 -> {
                    if (forCounting.getNome() != null)
                        count++;
                }
                case 1 -> {
                    if (forCounting.getPrezzo() != -1)
                        count++;
                }
                case 2 -> {
                    if (forCounting.getQuantita() != -1)
                        count++;
                }
                case 3 -> {
                    if (forCounting.getData_inserimento() != null)
                        count++;
                }
                case 4 -> {
                    if(forCounting.getDescrizione() != null)
                        count++;
                }
            }
            i++;
        }

        return count;

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
            case "Nome" -> "Nome = '" + value + "'";
            case "Descrizione" -> "Descrizione = '" + value + "'";
            case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
            default -> " ";
        };

    }

    @Override
    public void executeQuery(String query, boolean isOutput) {              // TODO: Rendi un unico metodo executeQuery e getRows

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

    public ResultSet getRows(boolean areAllRowsRequested, String inputQuery) {

        String toBeExecutedQuery;

        if (areAllRowsRequested)
            toBeExecutedQuery = "SELECT * FROM Articolo";
        else
            toBeExecutedQuery = inputQuery;

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            return statement.executeQuery(toBeExecutedQuery);
        }

        catch(SQLException e) {
                System.err.println("Errore durante l'ultima query: " + e.getMessage());
                return null;
        }

    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public int getNextItemCode() {
        return nextItemCode;
    }
}

