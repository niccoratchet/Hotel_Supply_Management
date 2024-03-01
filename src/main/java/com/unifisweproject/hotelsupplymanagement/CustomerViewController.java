package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {


    @FXML
    private TextField BusinessNameField;
    @FXML
    private TextField PivaField;
    @FXML
    private TextField NameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField SurnameField;
    @FXML
    private TextField FiscalCodeField;
    @FXML
    private TextField DiscountField;
    @FXML
    private TextField AddressField;
    @FXML
    private TextField CapField;
    @FXML
    private TextField CivicNumberField;

    @FXML
    private Label codeLabel;

    private Customer displayedCustomer;
    private CustomerManagementSceneController customerManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::addCustomerInfo);
    }

    public void addCustomerInfo() {

        codeLabel.setText("Dati cliente n°" + displayedCustomer.getCodice_cliente());         // TODO: Modifica deve essere possibile premerlo SOLO se è stata selezionata una riga
        BusinessNameField.setText(displayedCustomer.getRagione_sociale());
        PivaField.setText(displayedCustomer.getP_IVA());
        NameField.setText(displayedCustomer.getNome());
        SurnameField.setText(displayedCustomer.getCognome());
        FiscalCodeField.setText(displayedCustomer.getCodice_fiscale());
        DiscountField.setText(Integer.toString(displayedCustomer.getSconto()));
        AddressField.setText(displayedCustomer.getIndirizzo());
        CapField.setText(displayedCustomer.getCAP());
        CivicNumberField.setText(displayedCustomer.getCivico());
        datePicker.setValue(LocalDate.parse(displayedCustomer.getData_inserimento()));

    }

    public void setDisplayedCustomer(Customer displayedCustomer) {
        this.displayedCustomer = displayedCustomer;
    }

    public void modifyCustomer(ActionEvent event) {

        boolean error = false;
        int discount = 0;

        try {
            discount = Integer.parseInt(DiscountField.getText());
        }
        catch (NumberFormatException e) {
            System.err.println("Inserire un valore di sconto valido");
            error = true;
        }

        if (!error) {
            displayedCustomer.setRagione_sociale(BusinessNameField.getText());
            displayedCustomer.setP_IVA(PivaField.getText());
            displayedCustomer.setNome(NameField.getText());
            displayedCustomer.setCognome(SurnameField.getText());
            displayedCustomer.setCodice_fiscale(FiscalCodeField.getText());
            displayedCustomer.setSconto(discount);
            displayedCustomer.setIndirizzo(AddressField.getText());
            displayedCustomer.setCAP(CapField.getText());
            displayedCustomer.setCivico(CivicNumberField.getText());
            displayedCustomer.setData_inserimento(datePicker.getValue().toString());
        }

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void closeCustomerView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setCustomerManagementSceneController(CustomerManagementSceneController customerManagementSceneController) {
        this.customerManagementSceneController = customerManagementSceneController;
    }
}
