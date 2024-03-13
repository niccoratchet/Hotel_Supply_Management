package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class CustomerViewController implements Initializable {


    @FXML
    private TextField BusinessNameField;
    @FXML
    private TextField P_IVAField;
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

        int maxFiscalCodeCharacters = 16, maxDiscountCharacters = 2;

        TextFormatter<String> fiscalCodeFormatter = new TextFormatter<>(change -> {         // Formatter per il codice fiscale per impedire inserimento caratteri speciali e una lunghezza maggiore di 16 caratteri
            if (change.isDeleted()) {
                return change;
            }

            if (!change.getControlNewText().matches("[a-zA-Z0-9]*") && change.getControlNewText().length() <= maxFiscalCodeCharacters) {
                return null;
            }

            return change;
        });
        FiscalCodeField.setTextFormatter(fiscalCodeFormatter);

        UnaryOperator<TextFormatter.Change> filterDiscount = change -> {             // Creazione del Formatter per lo sconto: contiene unicamente numeri ed al massimo 2 cifre
            String text = change.getText();
            if (text.matches("[0-9]*") && change.getControlNewText().length() <= maxDiscountCharacters) {
                return change;
            }
            return null;
        };
        TextFormatter<String> discountFormatter = new TextFormatter<>(filterDiscount);
        DiscountField.setTextFormatter(discountFormatter);

    }

    public void addCustomerInfo() {

        codeLabel.setText("Dati cliente n°" + displayedCustomer.getCodice_cliente());         // TODO: Modifica deve essere possibile premerlo SOLO se è stata selezionata una riga
        BusinessNameField.setText(displayedCustomer.getRagione_sociale());
        P_IVAField.setText(displayedCustomer.getP_IVA());
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

            displayedCustomer.setRagione_sociale(BusinessNameField.getText());
            displayedCustomer.setP_IVA(P_IVAField.getText());
            displayedCustomer.setNome(NameField.getText());
            displayedCustomer.setCognome(SurnameField.getText());
            displayedCustomer.setCodice_fiscale(FiscalCodeField.getText());
            displayedCustomer.setSconto(Integer.parseInt(DiscountField.getText()));
            displayedCustomer.setIndirizzo(AddressField.getText());
            displayedCustomer.setCAP(CapField.getText());
            displayedCustomer.setCivico(CivicNumberField.getText());
            displayedCustomer.setData_inserimento(datePicker.getValue().toString());

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void closeCustomerView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setCustomerManagementSceneController(CustomerManagementSceneController customerManagementSceneController) {
        this.customerManagementSceneController = customerManagementSceneController;
    }
}
