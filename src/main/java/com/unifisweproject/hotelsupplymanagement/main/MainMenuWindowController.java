package com.unifisweproject.hotelsupplymanagement.main;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.customer.CustomerManagementWindowController;
import com.unifisweproject.hotelsupplymanagement.item.ItemManagementWindowController;
import com.unifisweproject.hotelsupplymanagement.order.OrderManagementWindowController;
import com.unifisweproject.hotelsupplymanagement.supplier.SupplierManagementWindowController;
import com.unifisweproject.hotelsupplymanagement.supply.SuppliesManagementWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static javax.security.auth.callback.ConfirmationCallback.INFORMATION;

public class MainMenuWindowController implements Initializable {


    private static final MainMenuWindowController instance = new MainMenuWindowController();        // Applicazione SingleTon per la finestra principale
    @FXML
    private Button openListOfOrdersButton;
    @FXML
    private Button openListOfSuppliersButton;
    @FXML
    private Button openListOfCustomersButton;
    @FXML
    private Button openListOfItemsButton;
    @FXML
    private Button openListOfSuppliesButton;
    @FXML
    private Button openCreditsButton;
    private static Stage stage;
    private boolean isMenuButton = false;               // Utile a conoscere se il cambio di area lavorativa (ad es.Item) avviene dalla pressione del bottone nel menù principale oppure tramite il MenuBar
    private final ArrayList<Boolean> isNotFirstTimeLoad = new ArrayList<>(4);       // Serve a capire quali delle 4 sezioni sono state aperte per la prima volta o meno ai fini di effettuare una singola interrogazione

    private MainMenuWindowController() {               // Costruttore privato per evitare la creazione di nuove istanze (SingleTon)
        for (int i = 0; i < 5; i++)
            isNotFirstTimeLoad.add(false);
    }

    public static MainMenuWindowController getInstance() {          // Metodo per ottenere l'istanza della classe (SingleTon)
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        openListOfItemsButton.setOnAction(this::openItemManagementView);
        openListOfCustomersButton.setOnAction(this::openCustomerManagementView);
        openListOfOrdersButton.setOnAction(this::openOrderManagementView);
        openListOfSuppliersButton.setOnAction(this::openSupplierManagementView);
        openListOfSuppliesButton.setOnAction(this::openSuppliesManagement);
        openCreditsButton.setOnAction(event -> openCredits());

    }

    public void openItemManagementView(ActionEvent event) {        // Metodo per l'apertura della finestra di gestione degli articoli

        try {
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemManagementWindow.fxml"),
                    ItemManagementWindowController.getInstance(), "Gestione Articoli", false, stage);
            verifyIsMenuButton(event);
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina ItemManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openCustomerManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione dei clienti

        try {
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerManagementWindow.fxml"),
                    CustomerManagementWindowController.getInstance(), "Gestione Clienti", false, stage);
            verifyIsMenuButton(event);
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina CustomerManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openOrderManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione degli ordini

        try {
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/order/OrderManagementWindow.fxml"),
                    OrderManagementWindowController.getInstance(),"Gestione Ordini", false, stage);
            verifyIsMenuButton(event);
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina OrderManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openSupplierManagementView(ActionEvent event) {         // Metodo per l'apertura della finestra di gestione dei fornitori

        try {
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierManagementWindow.fxml"),
                    SupplierManagementWindowController.getInstance(), "Gestione Fornitori", false, stage);
            verifyIsMenuButton(event);
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina SupplierManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openSuppliesManagement(ActionEvent event) {

        try {
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SuppliesManagementWindow.fxml"),
                    SuppliesManagementWindowController.getInstance() ,"Gestione Forniture", false, stage);
            verifyIsMenuButton(event);
        }
        catch (IOException e) {
            System.err.println("Non è stato possibile caricare la pagina SuppliesManagementWindow.fxml: " + e.getMessage());
        }

    }

    public void openMainMenuView() {          // Metodo per l'apertura del Menu principale a partire dalle altre sezioni del programma

        try {
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/main/MainMenuWindow.fxml"),    // FIXME: Probabilmente deve essere cambiato il modo  in cui viene passato lo stage a FXMLWindowLoader poiché ha bisogno di quello che è stato fornito dal metodo getStageFormMenuBar
                    MainMenuWindowController.getInstance(), "Menu Principale", false, stage);
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
            case "Apri menu principale" -> openMainMenuView();
        }

    }

    public void openCredits() {
        HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Hotel Supply Management", "Software developed by Niccolò Redi, Lorenzo Gazzini and Edoardo Cravegni. \n For the SWE Exam of UNIFI (A.A.2023/2024.)");
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

    public void setStage(Stage stage) {
        MainMenuWindowController.stage = stage;
    }

}
