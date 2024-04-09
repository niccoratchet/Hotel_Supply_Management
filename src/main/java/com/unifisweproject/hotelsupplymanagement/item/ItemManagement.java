package com.unifisweproject.hotelsupplymanagement.item;

import com.unifisweproject.hotelsupplymanagement.data.Data_Management;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;

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
            System.out.println("Errore durante l'aggiunta del nuovo Item: " +e.getMessage() + " \n Query utilizzata: " + addQuery);
        }

    }

    @Override
    public void modify(Object value) {

        Item modified = (Item) value;
        String modifyQuery = "UPDATE Articolo SET " + getDataTypeForQuery("Nome", modified.getNome(), false) + ", "
                + getDataTypeForQuery("Prezzo", modified.getPrezzo(), false) + ", " + getDataTypeForQuery("Quantita", modified.getQuantita(), false) + ", "
                + getDataTypeForQuery("Descrizione", modified.getDescrizione(), false) + ", " + getDataTypeForQuery("Data_Inserimento", modified.getData_inserimento(), false) +
                " WHERE Codice_Articolo = " + modified.getCodice_articolo();

        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            statement.setString(1, modified.getNome());
            statement.setString(2, modified.getDescrizione());
            executeQuery(false, statement);
        }

        catch (SQLException e) {
            System.err.println("Errore di formattazione nella generazione della query di modifica: " + e.getMessage());
        }

    }


    public ArrayList<Object> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Item che corrispondono alle righe indicate

        ArrayList<Object> results = new ArrayList<>();                // conterrà gli Item che corrispondono ai valori trovati dopo la query
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
            System.err.println("Errore durante la creazione del risultato di ricerca: " + e.getMessage());
            return null;
        }
        return results;

    }

    @Override
    public ArrayList<Object> search(Object toBeSearched) {

        Item item = (Item) toBeSearched;
        int numberOfParameters = getNumberOfParameters(item), numQuestionMarks = 0;
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Articolo WHERE ");
        boolean isNamePresent = false, isDescriptionPresent = false;
        int i = 0;
        while (i < 6 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(item.getNome() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Nome", item.getNome(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isNamePresent = true;
                        numQuestionMarks++;
                    }
                }
                case 1 -> {
                    if(item.getPrezzo() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Prezzo", item.getPrezzo(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(item.getQuantita() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Quantita", item.getQuantita(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 3 -> {
                    if(item.getData_inserimento() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_Inserimento", item.getData_inserimento(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 4 -> {
                    if(item.getDescrizione() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Descrizione", item.getDescrizione(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                        isDescriptionPresent = true;
                        numQuestionMarks++;
                    }
                }
                case 5 -> {
                    if(item.getCodice_articolo() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Articolo", item.getCodice_articolo(), true));
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
            while (i < 2 && numQuestionMarks > 0) {
                switch (i) {
                    case 0 -> {
                        if (isNamePresent) {
                            String nameValue = "%" + item.getNome() + "%";
                            statement.setString(parameterIndex, nameValue);
                            numQuestionMarks--;
                            parameterIndex++;
                        }
                    }
                    case 1 -> {
                        if (isDescriptionPresent) {
                            String surnameValue = "%" + item.getDescrizione() + "%";
                            statement.setString(parameterIndex, surnameValue);
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

    public int getNumberOfParameters(Item forCounting) {

        int i = 0, count = 0;
        while (i < 6) {
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
                case 5 -> {
                    if(forCounting.getCodice_articolo() != -1)
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
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM Articolo WHERE Codice_Articolo = " + code);
            executeQuery(false, statement);
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della riga Item: " + e.getMessage());
        }

    }

    public String getDataTypeForQuery(String dataType, Object value, boolean isSelect) {

        if (isSelect) {
            return switch (dataType) {
                case "Codice_Articolo" -> "Codice_Articolo = " + value;
                case "Prezzo" -> "Prezzo = " + value;
                case "Quantita" -> "Quantita = " + value;
                case "Nome" -> "Nome LIKE ?";                                // '?' verranno poi sostituiti dai valori corretti
                case "Descrizione" -> "Descrizione LIKE ?";
                case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
                default -> " ";
            };
        }
        else {
            return switch (dataType) {
                case "Codice_Articolo" -> "Codice_Articolo = " + value;
                case "Prezzo" -> "Prezzo = " + value;
                case "Quantita" -> "Quantita = " + value;
                case "Nome" -> "Nome = ?";                                // '?' verranno poi sostituiti dai valori corretti
                case "Descrizione" -> "Descrizione = ?";
                case "Data_Inserimento" -> "Data_Inserimento = '" + value + "'";
                default -> " ";
            };
        }

    }

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
                toBeExecutedQuery = "SELECT * FROM Articolo ORDER BY Data_Inserimento DESC";
                PreparedStatement allRowsQuery = HotelSupplyManagementMain.conn.prepareStatement(toBeExecutedQuery);
                return allRowsQuery.executeQuery();
            }
            return statement.executeQuery();
        }
        catch (SQLException e) {
            System.err.println("Errore durante il reperimento delle righe dalla tabella Articolo");
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

