package com.unifisweproject.hotelsupplymanagement.order;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class OrderSearchWindowController implements Initializable {
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
    @FXML
    private TextField customerCodeField;
    @FXML
    private TextField orderCodeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox enableOrderSearch;
    @FXML
    private CheckBox enablePaymentSearch;
    @FXML
    private CheckBox enableDateSearch;
    @FXML
    private CheckBox enableCustomerSearch;
    @FXML
    private Button confirmButton;
    private OrderManagementWindowController orderManagementWindowController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            enableOrderSearch.setOnAction(event -> handleCheckBoxAction(enableOrderSearch));
            enablePaymentSearch.setOnAction(event -> handleCheckBoxAction(enablePaymentSearch));
            enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
            enableCustomerSearch.setOnAction(event -> handleCheckBoxAction(enableCustomerSearch));
            typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");
        });

    }

    public void handleCheckBoxAction(CheckBox checkBox) {           // Metodo per l'attivazione o disattivazione dei filtri di ricerca

        if (checkBox.isSelected()) {
            verifyCheckBox(checkBox, false);
            enableConfirmButton();
        }
        else {
            verifyCheckBox(checkBox, true);
            enableConfirmButton();
        }

    }

    public void verifyCheckBox(CheckBox checkBox, boolean hasToBeEnabled) {            // Metodo utile a handleCheckBoxAction per verificare quale TextField attivare o disattivare

        switch (checkBox.getText()) {
            case "Codice_Ordine" -> {
                orderCodeField.setDisable(hasToBeEnabled);
                orderCodeField.setText("");
            }
            case "Tipo_Pagamento" -> typeOfPaymentField.setDisable(hasToBeEnabled);
            case "Data_Ordine" -> {
                datePicker.setDisable(hasToBeEnabled);
                datePicker.setValue(null);
            }
            case "Codice_Cliente" -> {
                customerCodeField.setDisable(hasToBeEnabled);
                customerCodeField.setText("");
            }
        }

    }
    public void enableConfirmButton() {
        confirmButton.setDisable(orderCodeField.isDisabled() && typeOfPaymentField.isDisabled() && datePicker.isDisabled() &&
                customerCodeField.isDisabled());
    }

    public void scanRows(ActionEvent event) {

        Order toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            try {
                orderManagementWindowController.searchRow(toBeSearched);
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
            catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setContentText("Parametri di ricerca vuoti: una volta spuntati inserire almeno un valore");
                alert.showAndWait();
            }
        }

    }

    public Order getSearchFilters() {

        Order searchItem = new Order(-1, -1, false,
                null, null);   // NOTA: è un oggetto order fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 4) {
                switch (i) {
                    case 0 -> {
                        if (!orderCodeField.isDisabled() && ! "".equals(orderCodeField.getText()))
                            searchItem.setCodice_ordine(Integer.parseInt(orderCodeField.getText()));
                    }
                    case 1 -> {
                        if (!typeOfPaymentField.isDisabled() && ! "".equals(typeOfPaymentField.getValue()))
                            searchItem.setTipo_pagamento(typeOfPaymentField.getValue());
                    }
                    case 2 -> {
                        if (!datePicker.isDisabled() && datePicker.getValue() != null)
                            searchItem.setData_ordine(datePicker.getValue().toString());
                    }
                    case 3 -> {
                        if (!customerCodeField.isDisabled() && ! "".equals(customerCodeField.getText()))
                            searchItem.setCodice_cliente(Integer.parseInt(customerCodeField.getText()));
                    }
                }
                i++;
            }
            return searchItem;
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Errore di formattazione: verifica i campi inseriti");
            alert.showAndWait();
            return null;
        }

    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setOrderManagementSceneController(OrderManagementWindowController orderManagementWindowController) {
        this.orderManagementWindowController = orderManagementWindowController;
    }

}
