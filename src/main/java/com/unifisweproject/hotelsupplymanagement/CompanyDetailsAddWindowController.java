package com.unifisweproject.hotelsupplymanagement;

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

public class CompanyDetailsAddWindowController implements Initializable {

    private CustomerAddWindowController customerAddWindowController = null;
    private SupplierAddWindowController supplierAddWindowController = null;
    @FXML
    private TextField P_IVAField;
    @FXML
    private TextField ragioneSocialeField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

    }

    public void confirmData(ActionEvent event) {

        if (P_IVAField.getText().length() == 11) {
            try {
                if ("".equals(ragioneSocialeField.getText()))
                    throw new RuntimeException();
                if (customerAddWindowController != null)
                    customerAddWindowController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
                if (supplierAddWindowController != null)
                    supplierAddWindowController.setCompanyDetails(P_IVAField.getText(), ragioneSocialeField.getText());
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

    public void resetFields() {
        P_IVAField.setText("");
        ragioneSocialeField.setText("");
    }

    public void executeQuery() {

        if ("".equals(P_IVAField.getText()) || "".equals(ragioneSocialeField.getText()))
            return;
        String insertCompanyDataQuery = "INSERT INTO Dati_Azienda (P_IVA, Ragione_Sociale) VALUES (" + P_IVAField.getText() + ", ?)";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(insertCompanyDataQuery);
            preparedStatement.setString(1, ragioneSocialeField.getText());
            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                System.out.println("Elemento già presente nel DB");
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la generazione della query in CompanyDetailsAddWindowController: " + e.getMessage());
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

    public void setAddCustomerViewController(CustomerAddWindowController customerAddWindowController) {
        this.customerAddWindowController = customerAddWindowController;
    }

    public void setAddSupplierViewController(SupplierAddWindowController supplierAddWindowController) {
        this.supplierAddWindowController = supplierAddWindowController;
    }

}
