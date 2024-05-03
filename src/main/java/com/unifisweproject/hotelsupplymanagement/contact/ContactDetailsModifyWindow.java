package com.unifisweproject.hotelsupplymanagement.contact;

import com.unifisweproject.hotelsupplymanagement.controller.customer.CustomerManagementController;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.controller.supplier.SupplierManagementController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ContactDetailsModifyWindow implements Initializable {

    @FXML
    private TextField addressField;
    @FXML
    private TextField civicNumberField;
    @FXML
    private TextField CAPField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField provinceField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField mailField;
    @FXML
    private Button confirmButton;
    private SupplierManagementController supplierController = null;
    private CustomerManagementController customerController = null;

    public ContactDetailsModifyWindow(SupplierManagementController supplierController) {
        this.supplierController = supplierController;
    }

    public ContactDetailsModifyWindow(CustomerManagementController customerController) {
        this.customerController = customerController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        confirmButton.setOnAction(this::confirmData);
        int capCharacters = 5, maxPhoneNumberCharacter = 10;
        civicNumberField.setTextFormatter(HotelSupplyManagementMain.getCivicNumberFormatter());
        CAPField.setTextFormatter(HotelSupplyManagementMain.getNumberOnlyStringFormatter(capCharacters));
        phoneNumberField.setTextFormatter(HotelSupplyManagementMain.getNumberOnlyStringFormatter(maxPhoneNumberCharacter));
        try {
            setInitialFields();
        } catch (SQLException e) {
            System.out.println("Errore nel caricamento dei dati: " + e.getMessage());
        }

    }

    public boolean isValidEmail(String email) {              //  una volta confermate delle modifiche viene effettuato il controllo sulla mail

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();

    }

    public void setInitialFields() throws SQLException {

        ResultSet resultSet;
        if (customerController != null) {
            resultSet = ContactDetailsManagement.setInitialFields(customerController.getDisplayedCustomer().getIndirizzo(),
                    customerController.getDisplayedCustomer().getCivico(), customerController.getDisplayedCustomer().getCAP());
        }
        else {
            resultSet = ContactDetailsManagement.setInitialFields(supplierController.getDisplayedSupplier().getIndirizzo(),
                    supplierController.getDisplayedSupplier().getCivico(), supplierController.getDisplayedSupplier().getCAP());
        }
        addressField.setText(resultSet.getString(1));
        civicNumberField.setText(resultSet.getString(3));
        CAPField.setText(resultSet.getString(2));
        locationField.setText(resultSet.getString(4));
        provinceField.setText(resultSet.getString(5));
        phoneNumberField.setText(resultSet.getString(6));
        mailField.setText(resultSet.getString(7));

    }

    public void confirmData(ActionEvent event) {

        try {
            verifyCAPLength();
            verifyEmptyField();
            if (!isValidEmail(mailField.getText())) {
                throw new RuntimeException("E-mail");
            }
            if (supplierController != null)
                supplierController.setContactDetails(addressField.getText(), CAPField.getText(), civicNumberField.getText());
            if (customerController != null)
                customerController.setContactDetails(addressField.getText(), CAPField.getText(), civicNumberField.getText());
            closeWindow(event);

        }
        catch (RuntimeException emptyFields) {
            generateAlert(emptyFields.getMessage());
        }

    }

    public void closeWindow(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void generateAlert(String alertType) {

        switch (alertType) {
            case "Parametro mancante" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "Parametri assenti", "E' necessario inserire tutti i parametri richiesti");
            case "CAP non valido" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "Formato CAP errato", "Il CAP deve contenere necessariamente 5 cifre");
            case "E-mail" -> HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore",
                    "Formato e-mail errato", "Inserisci una e-mail valida");
        }

    }

    public void verifyCAPLength() throws RuntimeException {
        if (CAPField.getText().length() != 5)
            throw new RuntimeException("CAP non valido");
    }

    public void verifyEmptyField() throws RuntimeException {

        if ("".equals(addressField.getText()) || "".equals(CAPField.getText()) || "".equals(civicNumberField.getText()) ||
                "".equals(locationField.getText()) || "".equals(provinceField.getText()) || "".equals(phoneNumberField.getText()) ||
                "".equals(mailField.getText()))
            throw new RuntimeException("Parametro mancante");

    }

    public TextField getLocationField() {
        return locationField;
    }

    public TextField getProvinceField() {
        return provinceField;
    }

    public TextField getPhoneNumberField() {
        return phoneNumberField;
    }

    public TextField getMailField() {
        return mailField;
    }

}
