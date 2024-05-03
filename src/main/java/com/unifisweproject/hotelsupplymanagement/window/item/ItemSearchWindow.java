package com.unifisweproject.hotelsupplymanagement.window.item;

import com.unifisweproject.hotelsupplymanagement.controller.item.ItemManagementController;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class ItemSearchWindow implements Initializable {

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
    @FXML
    private Button backButton;
    private final ItemManagementController controller;

    public ItemSearchWindow() {
        this.controller = ItemManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setOnActionCheckBox();
        priceField.setTextFormatter(HotelSupplyManagementMain.getDoubleFormatter());
        amountField.setTextFormatter(HotelSupplyManagementMain.getIntFormatter());
        confirmButton.setOnAction(this::searchItem);
        backButton.setOnAction(this::closeSearchView);

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

    public void searchItem(ActionEvent event) {
        controller.searchRow(event);
    }

    public void closeSearchView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    public TextField getPriceField() {
        return priceField;
    }

    public TextField getAmountField() {
        return amountField;
    }

    public TextField getCodeField() {
        return codeField;
    }

    public TextField getNameField() {
        return nameField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextField getDescriptionField() {
        return descriptionField;
    }

}
