package com.unifisweproject.hotelsupplymanagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderManagement implements Data_Management{
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
        String searchQuery = "SELECT * FROM Ordine WHERE ";

        switch (dataType) {

            case "Codice_Ordine":
                searchQuery += "Codice_Ordine = " + (Integer) value;
                break;
            case "BF":     //TODO Sistemare B/F tipo booleano
                searchQuery += "BF = " + (Boolean) value;
                break;
            case "Tipo_Pagamento":
                searchQuery += "Tipo_Pagamento = " + (String) value;
                break;
            case "Data_Ordine":
                searchQuery += "Data_Ordine = " + (String) value;
                break;
            case "Codice_Cliente":
                searchQuery += "Codice_Cliente = " + (Integer) value;
                break;
        }

        try {

            Statement statement = HotelSupplyManagementMain.conn.createStatement();

            ResultSet resultSet = statement.executeQuery(searchQuery);

            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1) + "\t" + resultSet.getBoolean(2) +
                        "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4) + "\t" +
                        resultSet.getInt(5));

            }

        }

        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public void printAll() {

        String printQuery = "SELECT * FROM Ordine";

        try {

            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(printQuery);

            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1) + "\t" + resultSet.getBoolean(2) +
                        "\t" + resultSet.getString(3) + "\t" + resultSet.getString(4) + "\t" +
                        resultSet.getInt(5));

            }

        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void print(Object tobeViewed) {

    }

    @Override
    public void delete(int code) {

        String deleteQuery = "DELETE FROM Ordine WHERE ";

    }
}
