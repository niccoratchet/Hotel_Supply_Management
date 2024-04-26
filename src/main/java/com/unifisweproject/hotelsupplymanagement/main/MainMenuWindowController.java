package com.unifisweproject.hotelsupplymanagement.main;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.customer.CustomerManagementController;
import com.unifisweproject.hotelsupplymanagement.item.ItemManagementController;
import com.unifisweproject.hotelsupplymanagement.order.OrderManagementController;
import com.unifisweproject.hotelsupplymanagement.supplier.SupplierManagementController;
import com.unifisweproject.hotelsupplymanagement.supply.SupplyManagementController;
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
        openListOfSuppliesButton.setOnAction(this::openSupplyManagement);
        openCreditsButton.setOnAction(event -> openCredits());

    }

    public void openItemManagementView(ActionEvent event) {        // Metodo per l'apertura della finestra di gestione degli articoli

        ItemManagementController itemManagementController = ItemManagementController.getInstance();
        itemManagementController.displayView(event);

    }

    public void openCustomerManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione dei clienti

        CustomerManagementController customerManagementController = CustomerManagementController.getInstance();
        customerManagementController.displayView(event);

    }

    public void openOrderManagementView(ActionEvent event) {      // Metodo per l'apertura della finestra di gestione degli ordini

        OrderManagementController orderManagementController = OrderManagementController.getInstance();
        orderManagementController.displayView(event);

    }

    public void openSupplierManagementView(ActionEvent event) {

        SupplierManagementController supplierManagementController = SupplierManagementController.getInstance();
        supplierManagementController.displayView(event);

    }

    public void openSupplyManagement(ActionEvent event) {

        SupplyManagementController supplyManagementController = SupplyManagementController.getInstance();
        supplyManagementController.displayView(event);

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

        MainMenuWindowController.stage = stage;
        isMenuButton = true;
        switch (sectionName) {
            case "Articoli in magazzino" -> openItemManagementView(event);
            case "Lista fornitori" -> openSupplierManagementView(event);
            case "Storico forniture" -> openSupplyManagement(event);
            case "Lista clienti" -> openCustomerManagementView(event);
            case "Storico ordini" -> openOrderManagementView(event);
            case "Apri menu principale" -> openMainMenuView();
        }

    }

    public void openCredits() {
        HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Credits", "Hotel Supply Management", "Software developed by Niccolò Redi, Lorenzo Gazzini and Edoardo Cravegni. \n For the SWE Exam of UNIFI (A.A.2023/2024.)");
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
