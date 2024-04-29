package com.unifisweproject.hotelsupplymanagement.order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class OrderSearchWindow implements Initializable {

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
    private final OrderDataManagementController controller;

    public OrderSearchWindow() {
        controller = OrderDataManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        enableOrderSearch.setOnAction(event -> handleCheckBoxAction(enableOrderSearch));
        enablePaymentSearch.setOnAction(event -> handleCheckBoxAction(enablePaymentSearch));
        enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
        enableCustomerSearch.setOnAction(event -> handleCheckBoxAction(enableCustomerSearch));
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");
        confirmButton.setOnAction(this::searchOrder);

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

    public void searchOrder(ActionEvent event) {
        controller.searchRow(event);
    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getCustomerCodeField() {
        return customerCodeField;
    }

    public ChoiceBox<String> getTypeOfPaymentField() {
        return typeOfPaymentField;
    }

    public TextField getOrderCodeField() {
        return orderCodeField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

}
