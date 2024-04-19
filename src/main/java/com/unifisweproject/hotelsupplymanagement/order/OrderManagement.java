package com.unifisweproject.hotelsupplymanagement.order;

import com.unifisweproject.hotelsupplymanagement.data.Data_Management;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;

import java.sql.*;
import java.util.ArrayList;

public class OrderManagement implements Data_Management {

    private static final OrderManagement instance = new OrderManagement();        // Singleton
    private int nextOrderCode;               // Tiene traccia del codice dell'ultimo Ordine nel DB
    private final ArrayList<Order> orderList = new ArrayList<>();

    private OrderManagement() {                                                                   // Il costruttore inizializza il contenuto della variabile nextItemCode

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

    public static OrderManagement getInstance() {
        return instance;
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

    }

    public ArrayList<Object> getSearchResults(ResultSet resultSet) {              // dato un oggetto ResultSet (insieme delle righe del risultato di una query) rende un ArrayList di Order che corrispondono alle righe indicate

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
        while (i < 4 && numberOfParameters > 0) {
            switch (i) {
                case 0 -> {
                    if(order.getCodice_cliente() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Cliente", order.getCodice_cliente(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 1 -> {
                    if(order.getTipo_pagamento() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Tipo_Pagamento", order.getTipo_pagamento(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 2 -> {
                    if(order.getData_ordine() != null) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Data_Ordine", order.getData_ordine(), true));
                        if (numberOfParameters != 0)
                            searchQuery.append(" AND ");
                    }
                }
                case 3 -> {
                    if(order.getCodice_ordine() != -1) {
                        numberOfParameters--;
                        searchQuery.append(getDataTypeForQuery("Codice_Ordine", order.getCodice_ordine(), true));
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
        while (i < 4) {
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
                case 3 -> {
                    if (forCounting.getCodice_ordine() != -1)
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