package com.unifisweproject.hotelsupplymanagement;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HotelSupplyManagementMain extends Application {

    public static Connection conn;

    @Override
    public void start(Stage primaryStage) {

        conn = null;

        try {

            String url = "jdbc:sqlite:C:\\Users\\Niccolò\\Desktop\\Work\\UNIFI\\Ingegneria del Software\\Hotel Supply Management\\hotel_supply_management.db";      // TODO: Da rendere possibile l'accesso a tutti
            conn = DriverManager.getConnection(url);
            System.out.println("Connection established");

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        MainMenuVBox mainMenuVBox = new MainMenuVBox();
        Scene mainMenuScene = new Scene(mainMenuVBox, 600, 400);
        primaryStage.setTitle("Hotel Supply Management");
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);

        ItemManagement itM = new ItemManagement();
        CustomerManagement cM = new CustomerManagement();
        OrderManagement oM = new OrderManagement();

        itM.delete(1);

        //cM.add(new Customer(20, "Edoardo", "Cravegni", "17/01/2024", "GH", "3", "Paolo", "Via", "5", "34"));

        try {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e) {
            System.out.println("Error during DB closure");
        }

    }

}