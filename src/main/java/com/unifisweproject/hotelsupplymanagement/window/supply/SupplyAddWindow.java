package com.unifisweproject.hotelsupplymanagement.window.supply;

import com.unifisweproject.hotelsupplymanagement.controller.supply.SupplyManagementController;
import com.unifisweproject.hotelsupplymanagement.model.item.Item;
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
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SupplyAddWindow implements Initializable {

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
    private ChoiceBox<String> supplierList;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private Button addItemsButton;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final SupplyManagementController controller;

    public SupplyAddWindow() {
        controller = SupplyManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        supplierList.setItems(controller.getSupplierList());
        datePicker.setValue(LocalDate.now());
        addButton.setOnAction(this::addSupply);
        backButton.setOnAction(this::closeAddView);
        addItemsButton.setOnAction(this::addItem);

    }


    public void setCellValueFactory() {

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn, itemPriceColumn, itemDescriptionColumn);

    }


    public void setRows(ObservableList<Item> rows) {

        itemList.setAll(rows);
        setCellValueFactory();
        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn, itemPriceColumn, itemDescriptionColumn);

    }


    public void addSupply(ActionEvent event) {
        controller.createRow(event);
    }

    public void addItem(ActionEvent event) {
        controller.openListOfItemView(event);
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public ObservableList<Item> getItemList() {
        return itemList;
    }

    public ChoiceBox<String> getSupplierList() { return supplierList; }

}
