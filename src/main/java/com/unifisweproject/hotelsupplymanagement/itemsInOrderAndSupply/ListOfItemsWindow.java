
/*
    Questa classe funge da controllore per la finestra che visualizza un piccolo riepilogo degli articoli presenti nel DB. Utile all'aggiunta degli articoli che
    compongono un ordine o una fornitura.
*/

package com.unifisweproject.hotelsupplymanagement.itemsInOrderAndSupply;

import com.unifisweproject.hotelsupplymanagement.model.item.Item;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.controller.order.OrderManagementController;
import com.unifisweproject.hotelsupplymanagement.controller.supply.SupplyManagementController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ListOfItemsWindow implements Initializable {

    @FXML
    private TableView<Item> itemTableView;
    @FXML
    private TableColumn<Item, Integer> itemCodeColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML
    private Label quantityLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    private OrderManagementController orderController = null;
    private SupplyManagementController supplyController = null;

    public ListOfItemsWindow(OrderManagementController orderController) {
        this.orderController = orderController;
    }

    public ListOfItemsWindow(SupplyManagementController supplyController) {
        this.supplyController = supplyController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setSelectionModel();
        setTextFieldListeners();
        setCellValueFactory();
        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn);
        quantityField.setTextFormatter(HotelSupplyManagementMain.getIntFormatter());
        priceField.setTextFormatter(HotelSupplyManagementMain.getDoubleFormatter());
        addButton.setOnAction(this::addItem);
        backButton.setOnAction(this::closeAddItem);

    }

    public void closeAddItem(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void addItem(ActionEvent event) {

        if (supplyController != null) {
            supplyController.addItemToSupply(event);
        }
        else
            orderController.addItemToOrder(event);

    }

    public void setCellValueFactory() {

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));

    }

    public void setTextFieldListeners() {

        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (supplyController != null) {
                addButton.setDisable(quantityField.getText().isEmpty() || priceField.getText().isEmpty());
            }
            else {
                addButton.setDisable(quantityField.getText().isEmpty());
            }
        });
        priceField.textProperty().addListener((observable, oldValue, newValue) -> addButton.setDisable(quantityField.getText().isEmpty() || priceField.getText().isEmpty()));

    }

    public void setSelectionModel() {

        itemTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {                      // Aggiunta del listener nella tabella per rilevare quale elemento viene selezionato
                quantityField.setVisible(true);
                quantityLabel.setVisible(true);
                quantityField.setDisable(false);
                if (supplyController != null) {
                    priceLabel.setVisible(true);
                    priceField.setVisible(true);
                    priceField.setDisable(false);
                }
            }
            else {
                quantityField.setVisible(false);
                quantityLabel.setVisible(false);
                quantityField.setDisable(true);
                if (supplyController != null) {
                    priceLabel.setVisible(false);
                    priceField.setVisible(false);
                    priceField.setDisable(true);
                }
            }
        });

    }

    public void setRows(ObservableList<Item> itemRows) {

        this.itemList.setAll(itemRows);
        setCellValueFactory();
        itemTableView.setItems(this.itemList);

    }

    public TableView<Item> getItemTableView() {
        return itemTableView;
    }

    public TextField getQuantityField() {
        return quantityField;
    }

    public TextField getPriceField() {
        return priceField;
    }

}
