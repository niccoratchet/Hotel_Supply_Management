package com.unifisweproject.hotelsupplymanagement;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

        ItemManagement itemManagement = new ItemManagement();

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScene.fxml"));
            Parent root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setItemManagement(itemManagement);

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

}