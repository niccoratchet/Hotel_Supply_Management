package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class OrderViewController implements Initializable {

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
    private Order displayedOrder;
    private OrderManagementSceneController orderManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(this::addOrderInfo);
        datePicker.setValue(LocalDate.now());
        BFField.getItems().addAll("Bolla", "Fattura");
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");

    }

    public void addOrderInfo() {

        String bf;
        codeLabel.setText("Dati ordine n°" + displayedOrder.getCodice_ordine());
        typeOfPaymentField.setValue(displayedOrder.getTipo_pagamento());
        datePicker.setValue(LocalDate.parse(displayedOrder.getData_ordine()));
        if(displayedOrder.isBolla()) {
            bf = "Bolla";
        }
        else {
            bf = "Fattura";
        }
        BFField.setValue(bf);

    }

    public void setDisplayedOrder(Order displayedOrder) {
        this.displayedOrder = displayedOrder;
    }

    public void closeOrderView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setOrderManagementSceneController(OrderManagementSceneController orderManagementSceneController) {
        this.orderManagementSceneController = orderManagementSceneController;
    }
}
