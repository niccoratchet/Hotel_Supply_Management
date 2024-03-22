package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OrderManagement implements Data_Management {

    private int nextOrderCode;               // Tiene traccia del codice dell'ultimo Ordine nel DB
    private final ArrayList<Order> orderList = new ArrayList<>();

    public OrderManagement() {                                                                   // Il costruttore inizializza il contenuto della variabile nextItemCode

        String getCodeQuery = "SELECT seq FROM sqlite_sequence WHERE name = 'Ordine'";

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            nextOrderCode = resultSet.getInt(1);
        }
        catch(SQLException e) {
            System.err.println("Errore durante l'estrapolazione dell'ultimo codice ordine");
        }

    }
    @Override
    public void add(Object newOrder) {

        Order toBeAdded = (Order) newOrder;

        String addQuery = "INSERT INTO Ordine (BF, Tipo_Pagamento, Data_Ordine, Codice_Cliente) \n" +       // creazione della query di inserimento
                "VALUES (?, ?, ?, ?)";

        try {

            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
            preparedStatement.setBoolean(1, toBeAdded.isBolla());
            preparedStatement.setString(2, toBeAdded.getTipo_pagamento());
            preparedStatement.setString(3, toBeAdded.getData_ordine());
            preparedStatement.setInt(4, toBeAdded.getCodice_cliente());
            preparedStatement.executeUpdate();                                                          // una volta creata, si invia il comando al DBMS
            nextOrderCode++;
        }
        catch (SQLException e) {
            System.out.println("Errore durante l'aggiunta del nuovo Order: "+ e.getMessage() +" \n Query utilizzata: " + addQuery);
        }
    }

    @Override
    public void modify(Object value) {
        Order modified = (Order) value;
        String modifyQuery = "UPDATE Ordine SET " + getDataTypeForQuery("Tipo_pagamento", modified.getTipo_pagamento(), false) + ", " + getDataTypeForQuery("Data_ordine", modified.getData_ordine(), false) + ", "
                + getDataTypeForQuery("Codice_cliente", modified.getCodice_cliente(), false)+
                " WHERE Codice_Ordine = " + modified.getCodice_ordine();
        //TODO: gestire attributo booleano ed aggiungere il formatter per tipo di pagamento
        try {
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            System.out.println(statement);
            executeQuery(false, statement);
        }

        catch (SQLException e) {
            System.err.println("Errore di formattazione nella generazione della query di modifica: " + e);
        }
    }

    public ArrayList<Object> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Item che corrispondono alle righe indicate

        ArrayList<Object> results = new ArrayList<>();                // conterrà gli Item che corrispondono ai valori trovati dopo la query

        try {
            while (resultSet.next()) {
                for (Order nextOrder : orderList) {
                    if (nextOrder.getCodice_ordine() == resultSet.getInt(1)) {
                        results.add(nextOrder);
                    }
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione del risultato di ricerca: " +e);
            return null;
        }
        return results;

    }
    @Override
    public ArrayList<Object> search(Object toBeSearched) {

        Order order = (Order) toBeSearched;
        int numberOfParameters = getNumberOfParameters(order);
        StringBuilder searchQuery = new StringBuilder("SELECT * FROM Ordine WHERE ");
        int i = 0;
        while (i < 3 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(order.getCodice_cliente() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_cliente", order.getCodice_cliente(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 1 -> {
                    if(order.getTipo_pagamento() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Tipo_pagamento", order.getTipo_pagamento(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(order.getData_ordine() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_ordine", order.getData_ordine(), true));
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
            System.err.println("Query di ricerca non correttamente formattata");
            return null;
        }
    }

    private int getNumberOfParameters(Order forCounting) {
        int i = 0, count = 0;
        while (i < 3) {
            switch (i) {
                case 0 -> {
                    if (forCounting.getCodice_cliente() != -1)
                        count++;
                }
                case 1 -> {
                    if (forCounting.getTipo_pagamento() != null)
                        count++;

                }
                case 2 -> {
                    if (forCounting.getData_ordine() != null)
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
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM Ordine WHERE Codice_Ordine = " + code);
            executeQuery(false, statement);
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione della riga Order: " + e.getMessage());
        }

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value, boolean isSelect) {

        return switch (dataType) {
            case "Codice_Ordine" -> "Codice_Ordine = " + value;
            case "BF" -> "BF = '" + value + "'";
            case "Tipo_Pagamento" -> "Tipo_Pagamento = '" + value + "'";
            case "Data_Ordine" -> "Data_Ordine = '" + value + "'";
            case "Codice_Cliente" -> "Codice_Cliente = " + value;
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
                toBeExecutedQuery = "SELECT * FROM Ordine ORDER BY Data_Ordine DESC";
                PreparedStatement allRowsQuery = HotelSupplyManagementMain.conn.prepareStatement(toBeExecutedQuery);
                return allRowsQuery.executeQuery();
            }
            return statement.executeQuery();
        }
        catch (SQLException e) {
            System.err.println("Errore durante il reperimento delle righe dalla tabella Order");
            return null;
        }

    }

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public int getNextOrderCode() {
        return nextOrderCode;
    }

}
