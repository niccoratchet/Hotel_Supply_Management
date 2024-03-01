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


public class SearchItemController implements Initializable {

    @FXML
    private TextField nameField;
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
    private CheckBox enablePriceSearch;
    @FXML
    private CheckBox enableAmountSearch;
    @FXML
    private CheckBox enableDateSearch;
    @FXML
    private CheckBox enableDescriptionSearch;
    @FXML
    private Button confirmButton;
    private ItemManagementSceneController itemManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            enableNameSearch.setOnAction(event -> handleCheckBoxAction(enableNameSearch));
            enablePriceSearch.setOnAction(event -> handleCheckBoxAction(enablePriceSearch));
            enableAmountSearch.setOnAction(event -> handleCheckBoxAction(enableAmountSearch));
            enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
            enableDescriptionSearch.setOnAction(event -> handleCheckBoxAction(enableDescriptionSearch));

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

            //TODO: Riutilizzare i formatter per i form delle altre finestre

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
        }

    }

    public void enableConfirmButton() {
        confirmButton.setDisable(nameField.isDisabled() && priceField.isDisabled() && amountField.isDisabled() && datePicker.isDisabled() && descriptionField.isDisabled());
    }

    public void scanRows(ActionEvent event) {

        Item toBeSearched = getSearchFilters();

        if (toBeSearched != null) {
            itemManagementSceneController.searchRow(toBeSearched);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

    }

    public Item getSearchFilters() {

        Item searchItem = new Item(-1, -1, null, null, null);   // NOTA: è un oggetto item fittizio utile alla ricerca

        int i = 0;

        try {
            while (i < 5) {
                switch (i) {
                    case 0 -> {
                        if (!nameField.isDisabled())
                            searchItem.setNome(nameField.getText());
                    }
                    case 1 -> {
                        if (!priceField.isDisabled())
                            searchItem.setPrezzo(Double.parseDouble(priceField.getText()));
                    }
                    case 2 -> {
                        if (!amountField.isDisabled()) {
                            searchItem.setQuantita(Integer.parseInt(amountField.getText()));
                        }
                    }
                    case 3 -> {
                        if (!datePicker.isDisabled())
                            searchItem.setData_inserimento(datePicker.getValue().toString());
                    }
                    case 4 -> {
                        if (!descriptionField.isDisabled())
                            searchItem.setDescrizione(descriptionField.getText());
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
            alert.setContentText("Valore del parametro 'Prezzo' non valido. \nRiprovare.");
            alert.showAndWait();
            return null;

        }

    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setItemManagementSceneController(ItemManagementSceneController itemManagementSceneController) {
        this.itemManagementSceneController = itemManagementSceneController;
    }

}
