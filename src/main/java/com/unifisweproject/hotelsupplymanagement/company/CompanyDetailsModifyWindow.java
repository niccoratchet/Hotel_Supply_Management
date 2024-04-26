package com.unifisweproject.hotelsupplymanagement.company;

import com.unifisweproject.hotelsupplymanagement.customer.CustomerManagementController;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.supplier.SupplierManagementController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CompanyDetailsModifyWindow implements Initializable {

    @FXML
    private TextField P_IVAField;
    @FXML
    private TextField ragioneSocialeField;
    @FXML
    private Button confirmButton;
    private SupplierManagementController supplierController = null;
    private CustomerManagementController customerController = null;

    public CompanyDetailsModifyWindow(SupplierManagementController supplierDisplayWindow) {
        this.supplierController = supplierDisplayWindow;
    }

    public CompanyDetailsModifyWindow(CustomerManagementController customerController) {
        this.customerController = customerController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        confirmButton.setOnAction(this::confirmData);
        int maxP_IVACharacters = 11;
        P_IVAField.setTextFormatter(HotelSupplyManagementMain.getNumberOnlyStringFormatter(maxP_IVACharacters));
        if (supplierController != null)
            setInitialFields();
        if (customerController != null)
            setInitialFields();

    }

    public void setInitialFields() {

        if (supplierController != null) {
            P_IVAField.setText(supplierController.getDisplayedSupplier().getP_IVA());
            ragioneSocialeField.setText(supplierController.getDisplayedSupplier().getRagione_sociale());
        }
        else {
            P_IVAField.setText(customerController.getDisplayedCustomer().getP_IVA());
            ragioneSocialeField.setText(customerController.getDisplayedCustomer().getRagione_sociale());
        }

    }

    public void confirmData(ActionEvent event) {

        try {
            verifyEmptyFields();
            if (supplierController != null)
                supplierController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
            if (customerController != null)
                customerController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
            closeWindow(event);
        }
        catch (RuntimeException emptyRagioneSociale) {
            generateAlert(emptyRagioneSociale.getMessage());
        }

    }

    public void verifyEmptyFields() {

        if (P_IVAField.getText().length() != 11)
            throw new RuntimeException("P_IVA");
        if ("".equals(ragioneSocialeField.getText()))
            throw new RuntimeException("Ragione Sociale");

    }

    public void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void generateAlert(String alertType) {               // Genera un Alert con testo in base al tipo di errore

        switch (alertType) {
            case "P_IVA" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "P_IVA errata", "La P_IVA deve contenere unicamente 11 cifre");
            case "Ragione Sociale" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "Ragione sociale vuota", "Inserire la ragione sociale");
        }

    }
    public TextField getP_IVAField() {
        return P_IVAField;
    }

    public TextField getRagioneSocialeField() {
        return ragioneSocialeField;
    }

}
