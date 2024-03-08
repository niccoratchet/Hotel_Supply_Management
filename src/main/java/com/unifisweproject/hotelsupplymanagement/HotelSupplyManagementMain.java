package com.unifisweproject.hotelsupplymanagement;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class HotelSupplyManagementMain extends Application {

    public static Connection conn;

    @Override
    public void start(Stage stage) {

        conn = null;

        try {

            String url = "jdbc:sqlite:C:\\Users\\Niccolò\\Desktop\\Work\\UNIFI\\Ingegneria del Software\\Hotel Supply Management\\hotel_supply_management.db";      // TODO: Da rendere possibile l'accesso a tutti
            conn = DriverManager.getConnection(url);
            System.out.println("Connection established");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScene.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public static void main(String[] args) {

        launch(args);

        try {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e) {
            System.out.println("Error during DB closure");
        }

    }

    public static <newType, oldType> ArrayList<newType> castArrayList(ArrayList<oldType> list){             // Il metodo permette di fare il casting di una lista generica di Object in un altro tipo specificato
        ArrayList<newType> newlyCastedArrayList = new ArrayList<newType>();
        for(oldType listObject : list){
            newlyCastedArrayList.add((newType)listObject);
        }
        return newlyCastedArrayList;
    }

}