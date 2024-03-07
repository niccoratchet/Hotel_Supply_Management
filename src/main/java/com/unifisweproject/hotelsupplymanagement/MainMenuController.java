package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController {

    private Stage stage;
    private Scene scene;
    private final ItemManagement itemManagement = new ItemManagement();
    private final CustomerManagement customerManagement = new CustomerManagement();
    private final SupplierManagement supplierManagement = new SupplierManagement();
    private final OrderManagement orderManagement = new OrderManagement();

    public void openSceneManagementView(ActionEvent event) {        // Metodo per l'apertura della finestra di gestione degli articoli

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemManagementScene.fxml"));
            Parent root = loader.load();
            ItemManagementSceneController itemManagementSceneController = loader.getController();
            itemManagementSceneController.setItemManagement(itemManagement);
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
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
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina CustomerManagementScene.fxml: " + e);
        }

    }

    public void openSupplierManagementView(ActionEvent event) {         // Metodo per l'apertura della finestra di gestione dei fornitori

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierManagementScene.fxml"));
            Parent root = loader.load();
            SupplierManagementSceneController supplierManagementSceneController = loader.getController();
            supplierManagementSceneController.setSupplierManagement(supplierManagement);
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina SupplierManagementScene.fxml: " + e);
        }


    }

    public void openOrderManagementView(ActionEvent event) {            // Metodo per l'apertura della finestra di gestione degli ordini

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderManagementScene.fxml"));
            Parent root = loader.load();
            OrderManagementSceneController orderManagementSceneController = loader.getController();
            orderManagementSceneController.setOrderManagement(orderManagement);
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina OrderManagementScene.fxml: " + e);
        }
    }

}
