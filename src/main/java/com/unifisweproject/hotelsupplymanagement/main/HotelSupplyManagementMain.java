package com.unifisweproject.hotelsupplymanagement.main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class HotelSupplyManagementMain extends Application {

    public static Connection conn;
    public static Image icon =                      // Icona dell'applicazione
            new Image(Objects.requireNonNull(HotelSupplyManagementMain.class.getResourceAsStream("/com/unifisweproject/hotelsupplymanagement/Icon/HotelSupplyManagementIcon.png")));

    @Override
    public void start(Stage stage) {

        conn = null;
        try {
            String url = "jdbc:sqlite:hotel_supply_management.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connessione al DB effettuata con successo!");
            if(!isFirstAccess(stage)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/LoginWindow.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.getIcons().add(icon);
                    stage.setResizable(false);
                    stage.setTitle("Login");
                    stage.show();
                }
                catch(IOException e) {
                    System.err.println("Errore durante il caricamento della pagine del file LoginWindow.fxml: " + e.getMessage());
                }
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
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
        ArrayList<newType> newlyCastedArrayList = new ArrayList<>();
        for(oldType listObject : list){
            newlyCastedArrayList.add((newType)listObject);
        }
        return newlyCastedArrayList;
    }

    public boolean isFirstAccess(Stage stage) {

        boolean isFirstAccess = false;
        String filePath = "password.txt";
        File file = new File(filePath);
        if(file.length() == 0) {
            isFirstAccess = true;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/FirstAccessWindow.fxml"));
                Parent root = loader.load();
                stage.setTitle("Hotel Supply Management");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setResizable(false);
                stage.getIcons().add(HotelSupplyManagementMain.icon);
                stage.show();
            }
            catch (IOException e) {
                System.err.println("Errore durante il caricamento della pagina del file FirstAccessWindow.fxml: " + e.getMessage());
            }
        }

        return isFirstAccess;

    }

}