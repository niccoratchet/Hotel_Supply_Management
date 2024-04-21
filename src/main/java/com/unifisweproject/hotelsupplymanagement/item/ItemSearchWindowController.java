package com.unifisweproject.hotelsupplymanagement.item;

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


public class ItemSearchWindowController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField descriptionField;
    @FXML
    private CheckBox enableNameSearch;
    @FXML
    private CheckBox enableCodeSearch;
    @FXML
    private CheckBox enablePriceSearch;
    @FXML
    private CheckBox enableAmountSearch;
    @FXML
    private CheckBox enableDateSearch;
    @FXML
    private CheckBox enableDescriptionSearch;
    @FXML
    private Button confirmButton;
    private ItemManagementWindowController itemManagementWindowController;
    private boolean isBadFormatted = false;

    public ItemSearchWindowController() {
        this.itemManagementWindowController = ItemManagementWindowController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            setOnActionCheckBox();
            setFormatters();
        });

    }
    public void setFormatters() {

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
        TextFormatter<String> textFormatterDouble = new TextFormatter<>(filterDouble);
        priceField.setTextFormatter(textFormatterDouble);
        TextFormatter<String> textFormatterInt = new TextFormatter<>(filterInt);
        amountField.setTextFormatter(textFormatterInt);

    }

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
            case "Nome" -> {
                nameField.setDisable(hasToBeEnabled);
                nameField.setText("");
            }
            case "Prezzo" -> {
                priceField.setDisable(hasToBeEnabled);
                priceField.setText("");
            }
            case "Quantità" -> {
                amountField.setDisable(hasToBeEnabled);
                amountField.setText("");
            }
            case "Data" -> {
                datePicker.setDisable(hasToBeEnabled);
                datePicker.setValue(null);
            }
            case "Descrizione" -> {
                descriptionField.setDisable(hasToBeEnabled);
                descriptionField.setText("");
            }
            case "Codice" -> {
                codeField.setDisable(hasToBeEnabled);
                codeField.setText("");
            }
        }

    }

    public void enableConfirmButton() {
        confirmButton.setDisable(nameField.isDisabled() && priceField.isDisabled() && amountField.isDisabled()
                && datePicker.isDisabled() && descriptionField.isDisabled() && codeField.isDisabled());
    }

    public void scanRows(ActionEvent event) {

        Item toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            itemManagementWindowController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
        else if(isBadFormatted){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di formattazione");
            alert.setContentText("Valore del parametro 'Codice articolo' non valido. \nRiprovare.");
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

    public Item getSearchFilters() {

        Item searchItem = new Item(-1, -1, null, null, null);   // NOTA: è un oggetto item fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 6) {
                switch (i) {
                    case 0 -> {
                        if (!nameField.isDisabled())
                            if(! "".equals(nameField.getText()))
                                searchItem.setNome(nameField.getText());
                            else
                                return null;
                    }
                    case 1 -> {
                        if (!priceField.isDisabled())
                            if(! "".equals(priceField.getText()))
                                searchItem.setPrezzo(Double.parseDouble(priceField.getText()));
                            else
                                return null;
                    }
                    case 2 -> {
                        if (!amountField.isDisabled())
                            if(! "".equals(amountField.getText()))
                                searchItem.setQuantita(Integer.parseInt(amountField.getText()));
                            else
                                return null;
                    }
                    case 3 -> {
                        if (!datePicker.isDisabled())
                            if(datePicker.getValue() != null)
                                searchItem.setData_inserimento(datePicker.getValue().toString());
                            else
                                return null;
                    }
                    case 4 -> {
                        if (!descriptionField.isDisabled())
                            if(! "".equals(descriptionField.getText()))
                                searchItem.setDescrizione(descriptionField.getText());
                            else
                                return null;
                    }
                    case 5 -> {
                        if(!codeField.isDisabled())
                            if(! "".equals(codeField.getText()))
                                searchItem.setCodice_articolo(Integer.parseInt(codeField.getText()));
                            else
                                return null;
                    }
                }
                i++;
            }
            return searchItem;
        }
        catch (NumberFormatException e) {
            isBadFormatted = true;
            return null;
        }

    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setItemManagementSceneController(ItemManagementWindowController itemManagementWindowController) {
        this.itemManagementWindowController = itemManagementWindowController;
    }
}
