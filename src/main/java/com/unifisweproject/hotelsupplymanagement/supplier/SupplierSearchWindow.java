package com.unifisweproject.hotelsupplymanagement.supplier;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SupplierSearchWindow implements Initializable {

    @FXML
    private TextField ragioneSocialeField;
    @FXML
    private TextField PIVAField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField civicNumberField;
    @FXML
    private TextField CAPField;
    @FXML
    private TextField codeField;
    @FXML
    private CheckBox enableCodeSearch;
    @FXML
    private CheckBox enableRagioneSocialeSearch;
    @FXML
    private CheckBox enablePIVASearch;
    @FXML
    private CheckBox enableAddressSearch;
    @FXML
    private CheckBox enableCivicNumberSearch;
    @FXML
    private CheckBox enableCAPSearch;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    //private SupplierManagementController controller;

    public SupplierSearchWindow() {
        //this.controller = SupplierManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //setOnActionCheckBox();
        //confirmButton.setOnAction(this::searchSupplier);
        //backButton.setOnAction(this::closeSearchView);

    }
    /*

    public void setOnActionCheckBox() {

        enableNameSearch.setOnAction(event -> handleCheckBoxAction(enableNameSearch));
        enableCodeSearch.setOnAction(event -> handleCheckBoxAction(enableCodeSearch));
        enablePriceSearch.setOnAction(event -> handleCheckBoxAction(enablePriceSearch));
        enableAmountSearch.setOnAction(event -> handleCheckBoxAction(enableAmountSearch));
        enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
        enableDescriptionSearch.setOnAction(event -> handleCheckBoxAction(enableDescriptionSearch));

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
            case "Ragione Sociale" -> {
                ragioneSocialeField.setDisable(hasToBeEnabled);
                ragioneSocialeField.setText("");
            }
            case "P_IVA" -> {
                PIVAField.setDisable(hasToBeEnabled);
                PIVAField.setText("");
            }
            case "Indirizzo" -> {
                addressField.setDisable(hasToBeEnabled);
                addressField.setText("");
            }
            case "Codice" -> {
                codeField.setDisable(hasToBeEnabled);
                codeField.setText("");
            }
            case "Civico" -> {
                civicNumberField.setDisable(hasToBeEnabled);
                civicNumberField.setText("");
            }
            case "CAP" -> {
                CAPField.setDisable(hasToBeEnabled);
                CAPField.setText("");
            }
        }

    }

    public void enableConfirmButton() {
        confirmButton.setDisable(ragioneSocialeField.isDisabled() && PIVAField.isDisabled() && addressField.isDisabled()
                && codeField.isDisabled() && civicNumberField.isDisabled() && CAPField.isDisabled());
    }

    public void searchSupplier(ActionEvent event) {
        controller.searchRow(event);
    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getPivaField() {
        return PIVAField;
    }

    public TextField getRagioneSocialeField() {
        return ragioneSocialeField;
    }

    public TextField getCodeField() {
        return codeField;
    }

    public TextField getAddressField() { return addressField; }

    public DatePicker getCivicNumberField() {
        return civicNumberField;
    }

    public TextField getCapField() {
        return CAPField;
    }

     */

}
