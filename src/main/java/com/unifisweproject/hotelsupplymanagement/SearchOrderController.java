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
    private TextField typeOfPaymentField;
    @FXML
    private TextField customerCodeField;
    @FXML
    private TextField orderCodeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox enableBFSearch;
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
    @FXML
    private ChoiceBox<String> BFField;
    private OrderManagementSceneController orderManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            enableBFSearch.setOnAction(event -> handleCheckBoxAction(enableBFSearch));
            enableOrderSearch.setOnAction(event -> handleCheckBoxAction(enableOrderSearch));
            enablePaymentSearch.setOnAction(event -> handleCheckBoxAction(enablePaymentSearch));
            enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
            enableCustomerSearch.setOnAction(event -> handleCheckBoxAction(enableCustomerSearch));

            BFField.getItems().addAll("Bolla", "Fattura");

            UnaryOperator<TextFormatter.Change> filterDouble = change -> {              // Creazione del Formatter per inserimento del prezzo
                String text = change.getText();
                if (text.matches("[0-9]*\\.?[0-9]*")) {
                    return change;
                }
                return null;
            };

            UnaryOperator<TextFormatter.Change> filterInt = change -> {             // Creazione del Formatter per inserimento delle quantità
                String text = change.getText();
                if (text.matches("[0-9]*")) {
                    return change;
                }
                return null;
            };

            TextFormatter<String> textFormatterDouble = new TextFormatter<>(filterInt);
            customerCodeField.setTextFormatter(textFormatterDouble);

            TextFormatter<String> textFormatterInt = new TextFormatter<>(filterInt);
            orderCodeField.setTextFormatter(textFormatterInt);

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
            case "BollaFattura" -> {
                BFField.setDisable(hasToBeEnabled);
            }
            case "Codice_Ordine" -> {
                orderCodeField.setDisable(hasToBeEnabled);
                orderCodeField.setText("");
            }
            case "Tipo_Pagamento" -> {
                typeOfPaymentField.setDisable(hasToBeEnabled);
                typeOfPaymentField.setText("");
            }
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
        confirmButton.setDisable(BFField.isDisabled() && orderCodeField.isDisabled() && typeOfPaymentField.isDisabled() && datePicker.isDisabled() && customerCodeField.isDisabled());
    }

    public void scanRows(ActionEvent event) {

        Order toBeSearched = getSearchFilters();

        if (toBeSearched != null) {
            orderManagementSceneController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

    }

    public Order getSearchFilters() {

        Order searchItem = new Order(-1, -1, false, null, null);   // NOTA: è un oggetto item fittizio utile alla ricerca

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
                        if (!typeOfPaymentField.isDisabled() && ! "".equals(typeOfPaymentField.getText())) {
                            searchItem.setTipo_pagamento(typeOfPaymentField.getText());
                        }
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
