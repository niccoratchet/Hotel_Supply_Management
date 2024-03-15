package com.unifisweproject.hotelsupplymanagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;


public class AddOrderViewController implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> BFField;
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
    @FXML
    private TextField customerCodeField;
    @FXML
    private TableView<Item> itemTableView;
    @FXML
    private TableColumn<Item, Integer> itemCodeColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<Item, Double> itemPriceColumn;
    @FXML
    private TableColumn<Item, String> itemDescriptionColumn;
    @FXML
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    private Stage addItemStage;

    private OrderManagementSceneController orderManagementSceneController;
    private MainMenuController mainMenuController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
        BFField.getItems().addAll("Bolla", "Fattura");
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createOrder(ActionEvent event) {
        boolean bolla = BFField.getValue().equals("Bolla");

        Order newSupplier = new Order(Integer.parseInt(customerCodeField.getText()), bolla , typeOfPaymentField.getValue(), datePicker.getValue().toString());
        orderManagementSceneController.addRow(newSupplier);

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void openListOfItemView (ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOfItems.fxml"));
            Parent root = loader.load();
            ListOfItemsController listOfItemsController = loader.getController();
            listOfItemsController.setAddOrderViewController(this);
            listOfItemsController.setMainMenuController(mainMenuController);
            addItemStage = new Stage();
            addItemStage.setTitle("Aggiungi articoli all'ordine");
            addItemStage.initModality(Modality.APPLICATION_MODAL);
            addItemStage.setScene(new Scene(root));
            addItemStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ListOfItems.fxml: "+ e.getMessage());
        }

    }

    public void setOrderManagementSceneController(OrderManagementSceneController orderManagementSceneController) {
        this.orderManagementSceneController = orderManagementSceneController;
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void addRow(Item newItem){

        itemList.add(newItem);

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));

        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn, itemPriceColumn, itemDescriptionColumn);

    }

}
