package com.unifisweproject.hotelsupplymanagement.order;

import com.unifisweproject.hotelsupplymanagement.item.Item;
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

public class OrderDisplayWindow implements Initializable {

    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, Integer> codeColumn;
    @FXML
    private TableColumn<Item, String> nameColumn;
    @FXML
    private TableColumn<Item, Integer> amountColumn;
    @FXML
    private TableColumn<Item, String> descriptionColumn;
    @FXML
    private TableColumn<Item, Double> priceColumn;
    @FXML
    private ChoiceBox<String> BFField;
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label codeLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    private final Order displayedOrder;
    private final OrderManagementController controller;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();

    public OrderDisplayWindow(Order displayedOrder) {

        this.displayedOrder = displayedOrder;
        controller = OrderManagementController.getInstance();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        controller.displayOrderInfo();
        BFField.getItems().addAll("Bolla", "Fattura");
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");
        backButton.setOnAction(this::closeOrderView);
        deleteButton.setOnAction(this::deleteOrder);

    }


    public void closeOrderView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setCellValueFactory() {

        codeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));

    }

    public void setRows(ObservableList<Item> itemRows) {

        this.itemList.setAll(itemRows);
        setCellValueFactory();
        itemTable.setItems(this.itemList);

    }

    public void deleteOrder(ActionEvent event) {

        controller.deleteRow(displayedOrder);
        closeOrderView(event);

    }

    public Order getDisplayedOrder() {
        return displayedOrder;
    }

    public ObservableList<Item> getItemList() {
        return itemList;
    }

    public Label getCodeLabel() {
        return codeLabel;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public ChoiceBox<String> getTypeOfPaymentField() {
        return typeOfPaymentField;
    }

    public ChoiceBox<String> getBFField() {
        return BFField;
    }

}
