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

public class SearchCustomerController implements Initializable {

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

    private CustomerManagementSceneController customerManagementSceneController;
    private boolean isBadFormatted = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            enableNameSearch.setOnAction(event -> handleCheckBoxAction(enableNameSearch));
            enableSurnameSearch.setOnAction(event -> handleCheckBoxAction(enableSurnameSearch));
            enableDiscountSearch.setOnAction(event -> handleCheckBoxAction(enableDiscountSearch));
            enableCustomerCodeSearch.setOnAction(event -> handleCheckBoxAction(enableCustomerCodeSearch));
            enableFiscalCodeSearch.setOnAction(event -> handleCheckBoxAction(enableFiscalCodeSearch));
            enablePIVASearch.setOnAction(event -> handleCheckBoxAction(enablePIVASearch));
            enableRagioneSocialeSearch.setOnAction(event -> handleCheckBoxAction(enableRagioneSocialeSearch));
            enableIndirizzoSearch.setOnAction(event -> handleCheckBoxAction(enableIndirizzoSearch));
            enableCivicSearch.setOnAction(event -> handleCheckBoxAction(enableCivicSearch));
            enableCAPSearch.setOnAction(event -> handleCheckBoxAction(enableCAPSearch));

            // TODO: Inserire i formatter per il codice fiscale, PIVA, Civico, CAP

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

    public void scanRows(ActionEvent event) {

        Customer toBeSearched = getSearchFilters();
        if (toBeSearched != null && !isBadFormatted) {
            customerManagementSceneController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
        else if (isBadFormatted) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di formattazione");
            alert.setContentText("Valore del parametro 'Codice cliente' non valido. \nRiprovare.");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Parametri assenti");
            alert.setContentText("Hai spuntato dei parametri ma non hai inserito i valori corrispondenti. \nRiprovare.");
            alert.showAndWait();
        }

    }

    public Customer getSearchFilters() {

        Customer searchCustomer = new Customer(-1, null, null, null, null,
                null, null, null, null, null);                                          // NOTA: è un oggetto item fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 10) {
                switch (i) {
                    case 0 -> {
                        if (!nameField.isDisabled())
                            if (! "".equals(nameField.getText()))
                                searchCustomer.setNome(nameField.getText());
                            else
                                return null;
                    }
                    case 1 -> {
                        if (!discountField.isDisabled())
                            if(! "".equals(discountField.getText()))
                                searchCustomer.setSconto(Integer.parseInt(discountField.getText()));            // TODO: Verificare se vengono inseriti valori sensati di sconto
                            else
                                return null;
                    }
                    case 2 -> {
                        if (!surnameField.isDisabled())
                            if(! "".equals(surnameField.getText()))
                                searchCustomer.setCognome(surnameField.getText());
                            else
                                return null;
                    }
                    case 3 -> {
                        if (!customerCodeField.isDisabled())
                            if(! "".equals(customerCodeField.getText()))
                                searchCustomer.setCodice_cliente(Integer.parseInt(customerCodeField.getText()));
                            else
                                return null;
                    }
                    case 4 -> {
                        if (!fiscalCodeField.isDisabled())
                            if(! "".equals(fiscalCodeField.getText()))
                                searchCustomer.setCodice_fiscale(fiscalCodeField.getText());
                            else
                                return null;
                    }
                    case 5 -> {
                        if (!PIVAField.isDisabled())
                            if(! "".equals(PIVAField.getText()))
                                searchCustomer.setP_IVA(PIVAField.getText());
                            else
                                return null;
                    }
                    case 6 -> {
                        if (!ragioneSocialeField.isDisabled())
                            if(! "".equals(ragioneSocialeField.getText()))
                                searchCustomer.setRagione_sociale(ragioneSocialeField.getText());
                            else
                                return null;
                    }
                    case 7 -> {
                        if (!indirizzoField.isDisabled())
                            if (! "".equals(indirizzoField.getText()))
                                searchCustomer.setIndirizzo(indirizzoField.getText());
                            else
                                return null;
                    }
                    case 8 -> {
                        if (!CAPField.isDisabled())
                            if(! "".equals(CAPField.getText()))
                                searchCustomer.setCAP(CAPField.getText());
                            else
                                return null;
                    }
                    case 9 -> {
                        if (!civicNumberField.isDisabled())
                            if (! "".equals(civicNumberField.getText()))
                                searchCustomer.setCivico(civicNumberField.getText());
                            else
                                return null;
                    }
                }
                i++;
            }
            return searchCustomer;
        }
        catch (NumberFormatException e) {
            isBadFormatted = true;
            return null;
        }

    }

    public void closeSearchView(ActionEvent event) {                                        // TODO: Metodo uguali tra le diverse aree (possibile generalizzazione)
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setCustomerManagementSceneController(CustomerManagementSceneController customerManagementSceneController) {
        this.customerManagementSceneController = customerManagementSceneController;
    }

}
