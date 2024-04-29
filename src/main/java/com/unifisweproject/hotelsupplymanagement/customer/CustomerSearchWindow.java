package com.unifisweproject.hotelsupplymanagement.customer;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;
public class CustomerSearchWindow implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField discountField;
    @FXML
    private TextField customerCodeField;
    @FXML
    private TextField fiscalCodeField;
    @FXML
    private TextField PIVAField;
    @FXML
    private TextField ragioneSocialeField;
    @FXML
    private TextField CAPField;
    @FXML
    private TextField civicNumberField;
    @FXML
    private TextField indirizzoField;
    @FXML
    private CheckBox enableNameSearch;
    @FXML
    private CheckBox enableSurnameSearch;
    @FXML
    private CheckBox enableDiscountSearch;
    @FXML
    private CheckBox enableCustomerCodeSearch;
    @FXML
    private CheckBox enableFiscalCodeSearch;
    @FXML
    private CheckBox enablePIVASearch;
    @FXML
    private CheckBox enableRagioneSocialeSearch;
    @FXML
    private CheckBox enableIndirizzoSearch;
    @FXML
    private CheckBox enableCAPSearch;
    @FXML
    private CheckBox enableCivicSearch;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    private final CustomerDataManagementController controller;

    public CustomerSearchWindow(){
        controller = CustomerDataManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setOnActionCheckBox();
        int capCharacters = 5;
        fiscalCodeField.setTextFormatter(HotelSupplyManagementMain.getFiscalCodeFormatter());
        discountField.setTextFormatter(HotelSupplyManagementMain.getDiscountFormatter());
        civicNumberField.setTextFormatter(HotelSupplyManagementMain.getCivicNumberFormatter());
        CAPField.setTextFormatter(HotelSupplyManagementMain.getNumberOnlyStringFormatter(capCharacters));
        confirmButton.setOnAction(this::searchCustomer);
        backButton.setOnAction(this::closeSearchView);

    }

    public void setOnActionCheckBox() {

        enableNameSearch.setOnAction(event -> handleCheckBoxAction(enableNameSearch));
        enableSurnameSearch.setOnAction(event -> handleCheckBoxAction(enableSurnameSearch));
        enableDiscountSearch.setOnAction(event -> handleCheckBoxAction(enableDiscountSearch));
        enableCustomerCodeSearch.setOnAction(event -> handleCheckBoxAction(enableCustomerCodeSearch));
        enableFiscalCodeSearch.setOnAction(event -> handleCheckBoxAction(enableFiscalCodeSearch));
        enablePIVASearch.setOnAction(event -> handleCheckBoxAction(enablePIVASearch));
        enableRagioneSocialeSearch.setOnAction(event -> handleCheckBoxAction(enableRagioneSocialeSearch));
        enableIndirizzoSearch.setOnAction(event -> handleCheckBoxAction(enableIndirizzoSearch));
        enableCAPSearch.setOnAction(event -> handleCheckBoxAction(enableCAPSearch));
        enableCivicSearch.setOnAction(event -> handleCheckBoxAction(enableCivicSearch));

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
            case "Nome" -> {
                nameField.setDisable(hasToBeEnabled);
                nameField.setText("");
            }
            case "Cognome" -> {
                surnameField.setDisable(hasToBeEnabled);
                surnameField.setText("");
            }
            case "Sconto" -> {
                discountField.setDisable(hasToBeEnabled);
                discountField.setText("");
            }
            case "Codice" -> {
                customerCodeField.setDisable(hasToBeEnabled);
                customerCodeField.setText("");
            }
            case "Codice Fiscale" -> {
                fiscalCodeField.setDisable(hasToBeEnabled);
                fiscalCodeField.setText("");
            }
            case "P_IVA" -> {
                PIVAField.setDisable(hasToBeEnabled);
                PIVAField.setText("");
            }
            case "Ragione Sociale" -> {
                ragioneSocialeField.setDisable(hasToBeEnabled);
                ragioneSocialeField.setText("");
            }
            case "Indirizzo" -> {
                indirizzoField.setDisable(hasToBeEnabled);
                indirizzoField.setText("");
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
        confirmButton.setDisable(nameField.isDisabled() && surnameField.isDisabled() && discountField.isDisabled() && customerCodeField.isDisabled() &&
                fiscalCodeField.isDisabled() && PIVAField.isDisabled() && ragioneSocialeField.isDisabled() && indirizzoField.isDisabled() &&
                civicNumberField.isDisabled() && CAPField.isDisabled());
    }


    public void searchCustomer(ActionEvent event) {
        controller.searchRow(event);
    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getSurnameField() {
        return surnameField;
    }

    public TextField getDiscountField() {
        return discountField;
    }

    public TextField getCustomerCodeField() {
        return customerCodeField;
    }

    public TextField getFiscalCodeField() {
        return fiscalCodeField;
    }

    public TextField getPIVAField() {
        return PIVAField;
    }

    public TextField getRagioneSocialeField() {
        return ragioneSocialeField;
    }

    public TextField getCAPField() {
        return CAPField;
    }

    public TextField getCivicNumberField() {
        return civicNumberField;
    }

    public TextField getIndirizzoField() {
        return indirizzoField;
    }

}
