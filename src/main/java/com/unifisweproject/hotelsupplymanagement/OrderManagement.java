package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderManagement implements Data_Management {
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

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modify(int code, String dataType, Object value) {

    }

    @Override
    public Object search(String dataType, Object value) {

        String searchQuery = "SELECT * FROM Ordine WHERE " + getDataTypeForQuery(dataType, value);
        executeQuery(searchQuery,true);
        return null;
    }

    @Override
    public void printAll() {

        String printQuery = "SELECT * FROM Ordine";
        executeQuery(printQuery, true);

    }

    @Override
    public void print(int code) {

    }

    @Override
    public void delete(int code) {
        String deleteQuery = "DELETE FROM Ordine WHERE ";

    }

    @Override
    public String getDataTypeForQuery(String dataType, Object value) {

        return switch (dataType) {
            case "Codice_Ordine" -> "Codice_Ordine = " + value;
            case "BF" -> "BF = " + value;
            case "Tipo_Pagamento" -> "Tipo_Pagamento = " + value;
            case "Data_Ordine" -> "Data_Ordine = " + value;
            case "Codice_Cliente" -> "Codice_Cliente = " + value;
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
                    System.out.println(resultSet.getInt(1) + "\t" + resultSet.getBoolean(2) +
                            "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4) + "\t" +
                            resultSet.getInt(5));

                }
            }

            else
                statement.executeUpdate(query);

        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

}
