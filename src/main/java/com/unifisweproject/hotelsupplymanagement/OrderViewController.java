package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public abstract class OrderViewController implements Initializable{
    @FXML
    private TextField BFField;
    @FXML
    private TextField TypeOfPaymentField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField CustomerCodeField;

    @FXML
    private Label codeLabel;

    private Order displayedOrder;
    private OrderManagementSceneController orderManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::addOrderInfo);
    }

    public void addOrderInfo() {

        codeLabel.setText("Dati ordine n°" + displayedOrder.getCodice_ordine());
        //TODO: gestire il valore booleano
        TypeOfPaymentField.setText(displayedOrder.getTipo_pagamento());
        datePicker.setValue(LocalDate.parse(displayedOrder.getData_ordine()));
        CustomerCodeField.setText(Integer.toString(displayedOrder.getCodice_ordine()));
    }

    public void setDisplayedOrder(Order displayedOrder) {
        this.displayedOrder = displayedOrder;
    }

    public void modifyOrder(ActionEvent event) {

        int code = 0;
        boolean error = false;

        try {
            code = Integer.parseInt(CustomerCodeField.getText());
        }
        catch (NumberFormatException e) {
            System.err.println("Inserire un valore di sconto valido");
            error = true;
        }

        if (!error) {
            //TODO: gestire il fatto che BF sia boolean
            displayedOrder.setTipo_pagamento(TypeOfPaymentField.getText());
            displayedOrder.setCodice_cliente(code);
            displayedOrder.setData_ordine(datePicker.getValue().toString());
        }

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void closeOrderView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setOrderManagementSceneController(OrderManagementSceneController orderManagementSceneController) {
        this.orderManagementSceneController = orderManagementSceneController;
    }
}
