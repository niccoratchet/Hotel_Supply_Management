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

public class SearchSupplierController implements Initializable {

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
    private SupplierManagementSceneController supplierManagementSceneController;

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
            supplierManagementSceneController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

    }

    public Supplier getSearchFilters() {

        Supplier searchSupplier = new Supplier(-1,null, null, null, null, null, null);     // NOTA: è un oggetto supplier fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 6) {
                switch (i) {
                    case 0 -> {
                        if (!ragioneSocialeField.isDisabled() && ! "".equals(ragioneSocialeField.getText()))
                            searchSupplier.setRagione_sociale(ragioneSocialeField.getText());
                    }
                    case 1 -> {
                        if (!PIVAField.isDisabled() && ! "".equals(PIVAField.getText()))
                            searchSupplier.setP_IVA(PIVAField.getText());
                    }
                    case 2 -> {
                        if (!addressField.isDisabled() && ! "".equals(addressField.getText())) {
                            searchSupplier.setIndirizzo(addressField.getText());
                        }
                    }
                    case 3 -> {
                        if (!codeField.isDisabled() && ! "".equals(codeField.getText()))
                            searchSupplier.setCodice_fornitore(Integer.parseInt(codeField.getText()));
                    }
                    case 4 -> {
                        if (!civicNumberField.isDisabled() && ! "".equals(civicNumberField.getText()))
                            searchSupplier.setCivico(civicNumberField.getText());
                    }
                    case 5 -> {
                        if (!CAPField.isDisabled() && ! "".equals(CAPField.getText()))
                            searchSupplier.setCAP(CAPField.getText());
                    }
                }
                i++;
            }
            return searchSupplier;

        }

        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.showAndWait();
            return null;
        }

    }

    public void setSupplierManagementSceneController(SupplierManagementSceneController supplierManagementSceneController) {
        this.supplierManagementSceneController = supplierManagementSceneController;
    }

}
