package com.unifisweproject.hotelsupplymanagement.supply;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.order.OrderManagementController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SupplySearchWindow implements Initializable {

    @FXML
    private ChoiceBox<String> companyField;
    @FXML
    private TextField itemCodeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox enableCompanySearch;
    @FXML
    private CheckBox enableItemSearch;
    @FXML
    private CheckBox enableDateSearch;
    @FXML
    private Button confirmButton;
    private final SupplyManagementController controller;

    public SupplySearchWindow() {
        controller = SupplyManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        enableCompanySearch.setOnAction(event -> handleCheckBoxAction(enableCompanySearch));
        enableItemSearch.setOnAction(event -> handleCheckBoxAction(enableItemSearch));
        enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
        confirmButton.setOnAction(this::searchSupply);
        companyField.setItems(controller.getSupplierList());

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
            case "Articolo" -> {
                itemCodeField.setDisable(hasToBeEnabled);
                itemCodeField.setText("");
            }
            case "Data" -> {
                datePicker.setDisable(hasToBeEnabled);
                datePicker.setValue(null);
            }
            case "Azienda" -> companyField.setDisable(hasToBeEnabled);
        }

    }

    public void enableConfirmButton() {                                                                                 // Metodo per l'abilitazione del pulsante di conferma
        confirmButton.setDisable(companyField.isDisabled() && itemCodeField.isDisabled() && datePicker.isDisabled());
    }


    public void searchSupply(ActionEvent event) {
        controller.searchRow(event);
    }


    public static void closeView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    public ChoiceBox<String> getCompanyField() {
        return companyField;
    }

    public TextField getItemCodeField() {
        return itemCodeField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

}
