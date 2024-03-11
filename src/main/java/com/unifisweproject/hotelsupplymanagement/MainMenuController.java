package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class MainMenuController {

    private Stage stage;
    private Scene scene;
    private final ItemManagement itemManagement = new ItemManagement();
    private final CustomerManagement customerManagement = new CustomerManagement();
    private final SupplierManagement supplierManagement = new SupplierManagement();
    private final OrderManagement orderManagement = new OrderManagement();
    private boolean isMenuButton = false;               // Utile a conoscere se il cambio di area lavorativa (ad es.Item) avviene dalla pressione del bottone nel menù principale oppure tramite il MenuBar
    private final ArrayList<Boolean> isNotFirstTimeLoad = new ArrayList<>(4);       // Serve a capire quali delle 4 sezioni sono state aperte per la prima volta o meno ai fini di effettuare una singola interrogazione

    public MainMenuController() {               // Inizializza il contenuto del vettore isNotFirstTimeLoad con tutti valori false
        for (int i = 0; i < 4; i++)
            isNotFirstTimeLoad.add(false);
    }

    public void openItemManagementView(ActionEvent event) {        // Metodo per l'apertura della finestra di gestione degli articoli

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemManagementScene.fxml"));
            Parent root = loader.load();
            ItemManagementSceneController itemManagementSceneController = loader.getController();
            itemManagementSceneController.setItemManagement(itemManagement);
            itemManagementSceneController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina ItemManagementScene.fxml: " + e);
        }

    }

    public void openCustomerManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione dei clienti

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerManagementScene.fxml"));
            Parent root = loader.load();
            CustomerManagementSceneController customerManagementSceneController = loader.getController();
            customerManagementSceneController.setCustomerManagement(customerManagement);
            customerManagementSceneController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina CustomerManagementScene.fxml: " + e);
        }

    }

    public void openOrderManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione degli ordini

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderManagementScene.fxml"));
            Parent root = loader.load();
            OrderManagementSceneController orderManagementSceneController = loader.getController();
            orderManagementSceneController.setOrderManagement(orderManagement);
            orderManagementSceneController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina OrderManagementScene.fxml: " + e);
        }

    }

    public void openSupplierManagementView(ActionEvent event) {         // Metodo per l'apertura della finestra di gestione dei fornitori

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierManagementScene.fxml"));
            Parent root = loader.load();
            SupplierManagementSceneController supplierManagementSceneController = loader.getController();
            supplierManagementSceneController.setSupplierManagement(supplierManagement);
            supplierManagementSceneController.setMainMenuController(this);
            verifyIsMenuButton(event);
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina SupplierManagementScene.fxml: " + e);
        }


    }

    public void getStageFromMenuBar(ActionEvent event, Stage stage, String sectionName) {           // Chiamato da un certo Controller dopo la pressione di un MenuItem. Serve a capire quale Management è stato premuto

        this.stage = stage;
        isMenuButton = true;
        switch (sectionName) {
            case "Articoli" -> openItemManagementView(event);
            case "Fornitori" -> openSupplierManagementView(event);
            case "Clienti" -> openCustomerManagementView(event);
            case "Ordini" -> openOrderManagementView(event);
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

}
