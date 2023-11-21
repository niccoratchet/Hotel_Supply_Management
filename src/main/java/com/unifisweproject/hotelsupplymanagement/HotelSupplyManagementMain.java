package com.unifisweproject.hotelsupplymanagement;


import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HotelSupplyManagementMain extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(50);

        HBox welcomeTextBox = new HBox();
        welcomeTextBox.setAlignment(Pos.CENTER);
        welcomeTextBox.setSpacing(50);

        Text welcomeText = new Text("Bentornato nel programma gestionale dell'azienda Breig SPA! \n Clicca uno dei bottoni per accedere ad una delle sezioni.");
        welcomeTextBox.getChildren().add(welcomeText);


        VBox buttonsContainer = new VBox();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setSpacing(50);

        HBox firstButtons = new HBox();
        firstButtons.setAlignment(Pos.CENTER);
        firstButtons.setSpacing(50);
        HBox secondButtons = new HBox();
        secondButtons.setAlignment(Pos.CENTER);
        secondButtons.setSpacing(50);

        Button manageCustomersButton = new Button("Gestione clienti");
        Button manageOrdersButton = new Button("Gestione ordini");
        Button manageSuppliersButton = new Button("Gestione fornitori");
        Button manageItemsButton = new Button("Gestione articoli");

        manageCustomersButton.setOnAction((actionEvent -> System.out.println("Gestione clienti!")));

        manageCustomersButton.setPrefSize(200, 100);
        manageOrdersButton.setPrefSize(200, 100);
        manageSuppliersButton.setPrefSize(200, 100);
        manageItemsButton.setPrefSize(200, 100);

        firstButtons.getChildren().add(manageCustomersButton);
        firstButtons.getChildren().add(manageOrdersButton);

        secondButtons.getChildren().add(manageSuppliersButton);
        secondButtons.getChildren().add(manageItemsButton);

        buttonsContainer.getChildren().add(firstButtons);
        buttonsContainer.getChildren().add(secondButtons);

        mainContainer.getChildren().add(welcomeTextBox);
        mainContainer.getChildren().add(buttonsContainer);


        Scene mainMenu = new Scene(mainContainer, 600, 400);

        primaryStage.setScene(mainMenu);
        primaryStage.setTitle("Hotel Supply Management");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}