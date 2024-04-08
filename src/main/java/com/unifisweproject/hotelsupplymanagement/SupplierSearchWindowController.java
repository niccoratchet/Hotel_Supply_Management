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

public class SupplierSearchWindowController implements Initializable {

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
    private SupplierManagementWindowController supplierManagementWindowController;
    private boolean isBadFormatted = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            enableRagioneSocialeSearch.setOnAction(event -> handleCheckBoxAction(enableRagioneSocialeSearch));
            enablePIVASearch.setOnAction(event -> handleCheckBoxAction(enablePIVASearch));
            enableAddressSearch.setOnAction(event -> handleCheckBoxAction(enableAddressSearch));
            enableCAPSearch.setOnAction(event -> handleCheckBoxAction(enableCAPSearch));
            enableCivicNumberSearch.setOnAction(event -> handleCheckBoxAction(enableCivicNumberSearch));
            enableCodeSearch.setOnAction(event -> handleCheckBoxAction(enableCodeSearch));
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

    public void scanRows(ActionEvent event) {

        Supplier toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            supplierManagementWindowController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
        else if(isBadFormatted){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di formattazione");
            alert.setContentText("Valore del parametro 'Codice fornitore' non valido. \nRiprovare.");
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

    public Supplier getSearchFilters() {

        Supplier searchSupplier = new Supplier(-1,null, null, null, null, null, null);     // NOTA: è un oggetto supplier fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 6) {
                switch (i) {
                    case 0 -> {
                        if (!ragioneSocialeField.isDisabled())
                            if(! "".equals(ragioneSocialeField.getText()))
                                searchSupplier.setRagione_sociale(ragioneSocialeField.getText());
                            else
                                return null;
                    }
                    case 1 -> {
                        if (!PIVAField.isDisabled())
                            if(! "".equals(PIVAField.getText()))
                                searchSupplier.setP_IVA(PIVAField.getText());
                            else
                                return null;
                    }
                    case 2 -> {
                        if (!addressField.isDisabled())
                            if(! "".equals(addressField.getText()))
                                searchSupplier.setIndirizzo(addressField.getText());
                            else
                                return null;
                    }
                    case 3 -> {
                        if (!codeField.isDisabled())
                            if(! "".equals(codeField.getText()))
                                searchSupplier.setCodice_fornitore(Integer.parseInt(codeField.getText()));
                            else
                                return null;
                    }
                    case 4 -> {
                        if (!civicNumberField.isDisabled())
                            if(! "".equals(civicNumberField.getText()))
                                searchSupplier.setCivico(civicNumberField.getText());
                            else
                                return null;
                    }
                    case 5 -> {
                        if (!CAPField.isDisabled())
                            if(! "".equals(CAPField.getText()))
                                searchSupplier.setCAP(CAPField.getText());
                            else
                                return null;
                    }
                }
                i++;
            }
            return searchSupplier;
        }
        catch (NumberFormatException e) {
            isBadFormatted = true;
            return null;
        }

    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    public void setSupplierManagementSceneController(SupplierManagementWindowController supplierManagementWindowController) {
        this.supplierManagementWindowController = supplierManagementWindowController;
    }

}
