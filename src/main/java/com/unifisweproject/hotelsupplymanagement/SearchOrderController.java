package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class SearchOrderController implements Initializable {
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
    private OrderManagementSceneController orderManagementSceneController;

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
            orderManagementSceneController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

    }

    public Order getSearchFilters() {

        Order searchItem = new Order(-1, -1, false,
                null, null);   // NOTA: è un oggetto order fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 5) {
                switch (i) {
                    case 0 -> {
                        if (!BFField.isDisabled() && ! "".equals(BFField.getValue()))
                            if(BFField.getValue().equals("bolla"))
                                searchItem.setBolla(true);
                            else searchItem.setBolla(false);//TODO: controllare se funziona
                    }
                    case 1 -> {
                        if (!orderCodeField.isDisabled() && ! "".equals(orderCodeField.getText()))
                            searchItem.setCodice_ordine(Integer.parseInt(orderCodeField.getText()));
                    }
                    case 2 -> {
                        if (!typeOfPaymentField.isDisabled() && ! "".equals(typeOfPaymentField.getValue()))
                            if(typeOfPaymentField.getValue().equals("Ricevuta bancaria"))
                                searchItem.setTipo_pagamento("Ricevuta bancaria");
                            else if (typeOfPaymentField.getValue().equals("Bonifico Bancario"))
                                searchItem.setTipo_pagamento("Bonifico bancario");
                                else if(typeOfPaymentField.getValue().equals("Rimessa diretta"))
                                    searchItem.setTipo_pagamento("Rimessa diretta");

                    }
                    case 3 -> {
                        if (!datePicker.isDisabled() && datePicker.getValue() != null)
                            searchItem.setData_ordine(datePicker.getValue().toString());
                    }
                    case 4 -> {
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
            alert.setHeaderText("Errore di formattazione");
            alert.showAndWait();
            return null;
        }

    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setOrderManagementSceneController(OrderManagementSceneController orderManagementSceneController) {
        this.orderManagementSceneController = orderManagementSceneController;
    }

}
