package com.unifisweproject.hotelsupplymanagement.company;

import com.unifisweproject.hotelsupplymanagement.customer.CustomerDisplayWindowController;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.supplier.SupplierDisplayWindowController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class CompanyDetailsModifyWindowController implements Initializable {

    @FXML
    private TextField P_IVAField;
    @FXML
    private TextField ragioneSocialeField;
    private SupplierDisplayWindowController supplierDisplayWindowController;
    private CustomerDisplayWindowController customerDisplayWindowController;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            int maxP_IVACharacters = 11;

            UnaryOperator<TextFormatter.Change> filterP_IVA = change -> {             // Creazione del Formatter per inserimento della partita IVA (contiene solo numeri e non può essere più lungo di 11 caratteri)
                String text = change.getText();
                if (text.matches("[0-9]*") && change.getControlNewText().length() <= maxP_IVACharacters) {
                    return change;
                }
                return null;
            };
            TextFormatter<String> p_IVAFormatter = new TextFormatter<>(filterP_IVA);
            P_IVAField.setTextFormatter(p_IVAFormatter);
            if (supplierDisplayWindowController != null)
                setInitialFields(supplierDisplayWindowController.getDisplayedSupplier().getP_IVA(), supplierDisplayWindowController.getDisplayedSupplier().getRagione_sociale());
            if (customerDisplayWindowController != null)
                setInitialFields(customerDisplayWindowController.getDisplayedCustomer().getP_IVA(), customerDisplayWindowController.getDisplayedCustomer().getRagione_sociale());

        });



    }

    public void setInitialFields(String P_IVA, String Ragione_Sociale) {
        P_IVAField.setText(P_IVA);
        ragioneSocialeField.setText(Ragione_Sociale);
    }

    public void confirmData(ActionEvent event) {

        if (P_IVAField.getText().length() == 11) {
            try {
                if ("".equals(ragioneSocialeField.getText()))
                    throw new RuntimeException();
                if (supplierDisplayWindowController != null)
                    supplierDisplayWindowController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
                if (customerDisplayWindowController != null)
                    customerDisplayWindowController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
            catch (RuntimeException emptyRagioneSociale) {
                generateAlert("Ragione Sociale");
            }
        }
        else {
            generateAlert("P_IVA");
        }

    }

    public void generateAlert(String alertType) {               // Genera un Alert con testo in base al tipo di errore

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        switch (alertType) {
            case "P_IVA" -> {
                alert.setHeaderText("P_IVA errata");
                alert.setContentText("La P_IVA deve contenere unicamente 11 cifre");
            }
            case "Ragione Sociale" -> {
                alert.setHeaderText("Ragione sociale vuota");
                alert.setContentText("Inserire la ragione sociale");
            }
        }
        alert.showAndWait();

    }

    public void executeQuery() {
        
        String modifyQuery = "UPDATE Dati_Azienda SET P_IVA = ?, Ragione_Sociale = ? WHERE P_IVA = ? AND Ragione_Sociale = ?";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
            preparedStatement.setString(1, P_IVAField.getText());
            preparedStatement.setString(2, ragioneSocialeField.getText());

            if (supplierDisplayWindowController != null)
                preparedStatement.setString(3, supplierDisplayWindowController.getDisplayedSupplier().getP_IVA()); // Parte dei parametri "vecchi" e quindi da cambiare
            if (customerDisplayWindowController != null)
                preparedStatement.setString(3, customerDisplayWindowController.getDisplayedCustomer().getP_IVA());

            if (supplierDisplayWindowController != null)
                preparedStatement.setString(4, supplierDisplayWindowController.getDisplayedSupplier().getRagione_sociale());
            if (customerDisplayWindowController != null)
                preparedStatement.setString(4, customerDisplayWindowController.getDisplayedCustomer().getRagione_sociale());

            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                System.out.println("Errore durante la query di modifica di Dati_Azienda: " + e.getMessage());
            }

        }
        catch (SQLException e) {
            System.err.println("Errore durante la generazione della query di modifica in Dati_Azienda: " + e.getMessage());
        }

    }
    public void setSupplierViewController(SupplierDisplayWindowController supplierDisplayWindowController) {
        this.supplierDisplayWindowController = supplierDisplayWindowController;
    }

    public void setCustomerViewController(CustomerDisplayWindowController customerDisplayWindowController) {
        this.customerDisplayWindowController = customerDisplayWindowController;
    }

}
