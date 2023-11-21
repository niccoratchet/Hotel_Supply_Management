package com.unifisweproject.hotelsupplymanagement;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HotelSupplyManagementMain extends Application {


    @Override
    public void start(Stage primaryStage) {


        MainMenuVBox mainMenuVBox = new MainMenuVBox();
        Scene mainMenuScene = new Scene(mainMenuVBox, 600, 400);

        primaryStage.setTitle("Hotel Supply Management");
        primaryStage.setScene(mainMenuScene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}