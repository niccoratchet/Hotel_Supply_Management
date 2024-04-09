package com.unifisweproject.hotelsupplymanagement.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static void connect() {

        Connection connectionToDB = null;

        try {

            Class.forName("org.sqlite.JDBC");
            connectionToDB = DriverManager.getConnection("jdbc:sqlite:hotel_supply_management.db");
            System.out.println("Connected!");

        }

        catch (ClassNotFoundException e) {
            System.err.println(e + "");

        }

        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
