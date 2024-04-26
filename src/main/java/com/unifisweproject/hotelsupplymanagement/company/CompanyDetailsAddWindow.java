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

public class CompanyDetailsAddWindow implements Initializable {

    private CustomerManagementController customerController = null;
    private SupplierManagementController supplierController = null;
    @FXML
    private TextField P_IVAField;
    @FXML
    private TextField ragioneSocialeField;
    @FXML
    private Button confirmButton;

    public CompanyDetailsAddWindow(CustomerManagementController customerController) {
        this.customerController = customerController;
    }

    public CompanyDetailsAddWindow(SupplierManagementController supplierController) {
        this.supplierController = supplierController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        int maxP_IVACharacters = 11;
        P_IVAField.setTextFormatter(HotelSupplyManagementMain.getNumberOnlyStringFormatter(maxP_IVACharacters));
        confirmButton.setOnAction(this::confirmData);

    }

    public void confirmData(ActionEvent event) {

        if (P_IVAField.getText().length() == 11) {
            try {
                if ("".equals(ragioneSocialeField.getText()))
                    throw new RuntimeException();
                if (customerController != null)
                    customerController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
                if (supplierController != null)
                    supplierController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
                closeWindow(event);
            }
            catch (RuntimeException emptyRagioneSociale) {
                generateAlert("Ragione Sociale");
            }
        }
        else {
            generateAlert("P_IVA");
        }

    }

    public void resetFields() {

        P_IVAField.setText("");
        ragioneSocialeField.setText("");

    }

    public void generateAlert(String alertType) {               // Genera un Alert con testo in base al tipo di errore

        switch (alertType) {
            case "P_IVA" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "P_IVA errata", "La P_IVA deve contenere unicamente 11 cifre");
            case "Ragione Sociale" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "Ragione sociale vuota", "Inserire la ragione sociale");
        }

    }

    public void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

}
