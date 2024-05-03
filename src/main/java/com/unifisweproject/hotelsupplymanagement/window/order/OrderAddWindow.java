package com.unifisweproject.hotelsupplymanagement.window.order;

import com.unifisweproject.hotelsupplymanagement.controller.order.OrderManagementController;
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

public class OrderAddWindow implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> BFField;
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
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
    private ChoiceBox<String> customerList;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private Button addItemButton;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final OrderManagementController controller;

    public OrderAddWindow() {
        controller = OrderManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        BFField.getItems().addAll("Bolla", "Fattura");
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");
        customerList.setItems(controller.getCustomerList());
        addButton.setOnAction(this::addOrder);
        backButton.setOnAction(this::closeAddView);
        addItemButton.setOnAction(this::addItem);

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

    public void addOrder(ActionEvent event) {
        controller.createRow(event);
    }

    public void addItem(ActionEvent event) {
        controller.openListOfItemView(event);
    }

    public void closeAddView(ActionEvent event) {

        controller.resetListOfItemsToAdd();
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

    }

    public ChoiceBox<String> getCustomerList() {
        return customerList;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    public ChoiceBox<String> getTypeOfPaymentField() {
        return typeOfPaymentField;
    }

    public ChoiceBox<String> getBFField() {
        return BFField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public ObservableList<Item> getItemList() {
        return itemList;
    }


}
