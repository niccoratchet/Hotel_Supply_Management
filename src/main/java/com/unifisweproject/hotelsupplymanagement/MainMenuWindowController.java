package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class MainMenuWindowController {

    private Stage stage;
    private Scene scene;
    private final ItemManagement itemManagement = new ItemManagement();
    private final CustomerManagement customerManagement = new CustomerManagement();
    private final SupplierManagement supplierManagement = new SupplierManagement();
    private final OrderManagement orderManagement = new OrderManagement();
    private final SuppliesManagement suppliesManagement = new SuppliesManagement();
    private boolean isMenuButton = false;               // Utile a conoscere se il cambio di area lavorativa (ad es.Item) avviene dalla pressione del bottone nel menù principale oppure tramite il MenuBar
    private final ArrayList<Boolean> isNotFirstTimeLoad = new ArrayList<>(4);       // Serve a capire quali delle 4 sezioni sono state aperte per la prima volta o meno ai fini di effettuare una singola interrogazione

    public MainMenuWindowController() {               // Inizializza il contenuto del vettore isNotFirstTimeLoad con tutti valori false
        for (int i = 0; i < 5; i++)
            isNotFirstTimeLoad.add(false);
    }

    public void openItemManagementView(ActionEvent event) {        // Metodo per l'apertura della finestra di gestione degli articoli

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemManagementWindow.fxml"));
            Parent root = loader.load();
            ItemManagementWindowController itemManagementWindowController = loader.getController();
            itemManagementWindowController.setItemManagement(itemManagement);
            itemManagementWindowController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina ItemManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openCustomerManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione dei clienti

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerManagementWindow.fxml"));
            Parent root = loader.load();
            CustomerManagementWindowController customerManagementWindowController = loader.getController();
            customerManagementWindowController.setCustomerManagement(customerManagement);
            customerManagementWindowController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina CustomerManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openOrderManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione degli ordini

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderManagementWindow.fxml"));
            Parent root = loader.load();
            OrderManagementWindowController orderManagementWindowController = loader.getController();
            orderManagementWindowController.setOrderManagement(orderManagement);
            orderManagementWindowController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina OrderManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openSupplierManagementView(ActionEvent event) {         // Metodo per l'apertura della finestra di gestione dei fornitori

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierManagementWindow.fxml"));
            Parent root = loader.load();
            SupplierManagementWindowController supplierManagementWindowController = loader.getController();
            supplierManagementWindowController.setSupplierManagement(supplierManagement);
            supplierManagementWindowController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina SupplierManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openSuppliesManagement(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SuppliesManagementWindow.fxml"));
            Parent root = loader.load();
            SuppliesManagementWindowController suppliesManagementWindowController = loader.getController();
            suppliesManagementWindowController.setSuppliesManagement(suppliesManagement);
            suppliesManagementWindowController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina SuppliesManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openMainMenuView(ActionEvent event) {          // Metodo per l'apertura del Menu principale a partire dalle altre sezioni del programma

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuWindow.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina MainMenuWindow.fxml: " + e.getMessage());
        }

    }

    public void getStageFromMenuBar(ActionEvent event, Stage stage, String sectionName) {           // Chiamato da un certo Controller dopo la pressione di un MenuItem. Serve a capire quale Management è stato premuto

        this.stage = stage;
        isMenuButton = true;
        switch (sectionName) {
            case "Articoli in magazzino" -> openItemManagementView(event);
            case "Lista fornitori" -> openSupplierManagementView(event);
            case "Storico forniture" -> openSuppliesManagement(event);
            case "Lista clienti" -> openCustomerManagementView(event);
            case "Storico ordini" -> openOrderManagementView(event);
            case "Apri menu principale" -> openMainMenuView(event);
        }

    }

    public void verifyIsMenuButton(ActionEvent event) {                         // Verifica e aggiorna la variabile isMenuButton per cambiare modalità di apertura nuovo Management
        if (!isMenuButton)
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        else
            isMenuButton = false;
    }

    public ArrayList<Boolean> getIsNotFirstTimeLoad() {
        return isNotFirstTimeLoad;
    }

    public ItemManagement getItemManagement() {
        return itemManagement;
    }

}
