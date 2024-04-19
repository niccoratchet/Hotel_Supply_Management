package com.unifisweproject.hotelsupplymanagement.main;


import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.login.FirstAccessWindowController;
import com.unifisweproject.hotelsupplymanagement.login.LoginWindowController;
import javafx.application.Application;
import javafx.scene.control.Alert;
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
    public static Image icon;

    @Override
    public void start(Stage stage) {

        conn = null;
        try {
            String url = "jdbc:sqlite:hotel_supply_management.db";
            connectToDB(url);
            icon = new Image(Objects.requireNonNull(HotelSupplyManagementMain.class.getResourceAsStream("/com/unifisweproject/hotelsupplymanagement/Icon/HotelSupplyManagementIcon.png")));
            if(!isFirstAccess(stage)) {
                try {
                    FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/LoginWindow.fxml"), LoginWindowController.getInstance(), false, null, "Hotel Supply Management", false);
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
                FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/FirstAccessWindow.fxml"), FirstAccessWindowController.getInstance(), false, null, "Hotel Supply Management", false);
            }
            catch (IOException e) {
                System.err.println("Errore durante il caricamento della pagina del file FirstAccessWindow.fxml: " + e.getMessage());
            }
        }
        return isFirstAccess;

    }

    public static void connectToDB(String url) throws SQLException {             // Il metodo permette di creare la connessione sia al DB principale che al DB di test
        conn = DriverManager.getConnection(url);
        System.out.println("Connessione al DB effettuata con successo!");
    }

    public static void generateAlert(Alert.AlertType alertType, String messageType, String messageContent) {

        Alert alert = new Alert(alertType);
        alert.setTitle("Avviso");
        alert.setHeaderText(messageType);
        alert.setContentText(messageContent);
        alert.showAndWait();

    }

    public static void openCredits() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText("Hotel Supply Management");
        alert.setContentText("Software developed by Niccolò Redi, Lorenzo Gazzini and Edoardo Cravegni. \n For the SWE Exam of UNIFI (A.A.2023/2024.)");
        alert.showAndWait();

    }

}