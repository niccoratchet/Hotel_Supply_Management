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
    private DatePicker datePicker;
    @FXML
    private TextField businessNameField;
    @FXML
    private TextField pivaField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField civicField;
    @FXML
    private TextField capField;
    @FXML
    private CheckBox enableBusinessNameSearch;
    @FXML
    private CheckBox enablePivaSearch;
    @FXML
    private CheckBox enableAddressSearch;
    @FXML
    private CheckBox enableCivicSearch;
    @FXML
    private CheckBox enableCapSearch;
    @FXML
    private CheckBox enableDateSearch;
    @FXML
    private Button confirmButton;
    private SupplierManagementSceneController supplierManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            enableBusinessNameSearch.setOnAction(event -> handleCheckBoxAction(enableBusinessNameSearch));
            enablePivaSearch.setOnAction(event -> handleCheckBoxAction(enablePivaSearch));
            enableAddressSearch.setOnAction(event -> handleCheckBoxAction(enableAddressSearch));
            enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
            enableCapSearch.setOnAction(event -> handleCheckBoxAction(enableCapSearch));
            enableCivicSearch.setOnAction(event -> handleCheckBoxAction(enableCivicSearch));

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
            case "Ragione_sociale" -> {
                businessNameField.setDisable(hasToBeEnabled);
                businessNameField.setText("");
            }
            case "Piva" -> {
                pivaField.setDisable(hasToBeEnabled);
                pivaField.setText("");
            }
            case "Indirizzo" -> {
                addressField.setDisable(hasToBeEnabled);
                addressField.setText("");
            }
            case "Data" -> {
                datePicker.setDisable(hasToBeEnabled);
                datePicker.setValue(null);
            }
            case "Civico" -> {
                civicField.setDisable(hasToBeEnabled);
                civicField.setText("");
            }
            case "Cap" -> {
                capField.setDisable(hasToBeEnabled);
                capField.setText("");
            }
        }

    }

    public void enableConfirmButton() {
        confirmButton.setDisable(businessNameField.isDisabled() && pivaField.isDisabled() && addressField.isDisabled() && datePicker.isDisabled() && civicField.isDisabled() && capField.isDisabled());
    }

    public void scanRows(ActionEvent event) {

        Supplier toBeSearched = getSearchFilters();

        if (toBeSearched != null) {
            supplierManagementSceneController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

    }

    public Supplier getSearchFilters() {

        Supplier searchSupplier = new Supplier(null, null, null, null, null, null);     // NOTA: è un oggetto supplier fittizio utile alla ricerca

        int i = 0;

        try {
            while (i < 6) {
                switch (i) {
                    case 0 -> {
                        if (!businessNameField.isDisabled() && ! "".equals(businessNameField.getText()))
                            searchSupplier.setRagione_sociale(businessNameField.getText());
                    }
                    case 1 -> {
                        if (!pivaField.isDisabled() && ! "".equals(pivaField.getText()))
                            searchSupplier.setP_IVA(pivaField.getText());
                    }
                    case 2 -> {
                        if (!addressField.isDisabled() && ! "".equals(addressField.getText())) {
                            searchSupplier.setIndirizzo(addressField.getText());
                        }
                    }
                    case 3 -> {
                        if (!datePicker.isDisabled() && datePicker.getValue() != null)
                            searchSupplier.setData_inserimento(datePicker.getValue().toString());
                    }
                    case 4 -> {
                        if (!civicField.isDisabled() && ! "".equals(civicField.getText()))
                            searchSupplier.setCivico(civicField.getText());
                    }
                    case 5 -> {
                        if (!capField.isDisabled() && ! "".equals(capField.getText()))
                            searchSupplier.setCAP(capField.getText());
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
