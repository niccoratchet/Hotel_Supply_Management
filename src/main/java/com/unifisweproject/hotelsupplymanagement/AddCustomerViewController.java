package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class AddCustomerViewController implements Initializable {


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

    private CustomerManagementSceneController customerManagementSceneController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //datePicker.setValue(LocalDate.now());
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createCustomer(ActionEvent event) {

        boolean error = false;
        int discount = 0;

        try {
            discount = Integer.parseInt(DiscountField.getText());
        }
        catch (NumberFormatException e) {
            System.err.println("Inserire un valore di Sconto valido");
            error = true;
        }

        if (!error) {
            Customer newCustomer = new Customer(discount, NameField.getText(), SurnameField.getText(), datePicker.getValue().toString(), FiscalCodeField.getText(), PivaField.getText(),
                    BusinessNameField.getText(), AddressField.getText(), CapField.getText(), CivicNumberField.getText());
            customerManagementSceneController.addRow(newCustomer);
        }
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void addContactDetailsView(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddContactDetails.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Aggiungi Recapiti");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();
        }
        catch(IOException e) {
            System.err.println("Errore durante il caricamento della pagina " + e);
        }

    }

    public void setCustomerManagementSceneController(CustomerManagementSceneController customerManagementSceneController) {
        this.customerManagementSceneController = customerManagementSceneController;
    }

    public void addContactDetails(ActionEvent event) {
    }

    public void closeAddContactDetails(ActionEvent event) {
    }
}
