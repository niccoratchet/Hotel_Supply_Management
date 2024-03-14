package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class AddCustomerViewController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField fiscalCodeField;
    @FXML
    private TextField discountField;
    @FXML
    private CheckBox privateCheckBox;
    @FXML
    private Button addCompanyDataButton;

    private CustomerManagementSceneController customerManagementSceneController;
    private AddContactDetails addContactDetails = null;         // TODO: Implementare collegamento tra le due view in modo da garantire la comunicazione
    private AddCompanyDetails addCompanyDetails = null;
    private final ArrayList<String> companyDetails = new ArrayList<>();
    private final ArrayList<String> contactDetails = new ArrayList<>();
    private Stage contactDetailsStage;
    private Stage companyDetailsStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        privateCheckBox.setOnAction(event -> disableCompanyData());
        int maxFiscalCodeCharacters = 16, maxDiscountCharacters = 2;

        UnaryOperator<TextFormatter.Change> filterFiscalCodeFormatter = change -> {             // Creazione del Formatter per lo sconto: contiene unicamente numeri e al massimo 2 cifre
            String newText = change.getControlNewText().toUpperCase();
            change.setText(newText);
            change.setRange(0, change.getControlText().length());
            String text = change.getText();
            if (text.matches("[a-zA-Z0-9]*") && change.getControlNewText().length() <= maxFiscalCodeCharacters) {
                return change;
            }
            return null;
        };
        TextFormatter<String> fiscalCodeFormatter = new TextFormatter<>(filterFiscalCodeFormatter);
        fiscalCodeField.setTextFormatter(fiscalCodeFormatter);

        UnaryOperator<TextFormatter.Change> filterDiscount = change -> {             // Creazione del Formatter per lo sconto: contiene unicamente numeri e al massimo 2 cifre
            String text = change.getText();
            if (text.matches("[0-9]*") && change.getControlNewText().length() <= maxDiscountCharacters) {
                return change;
            }
            return null;
        };
        TextFormatter<String> discountFormatter = new TextFormatter<>(filterDiscount);
        discountField.setTextFormatter(discountFormatter);

        try {
            loadContactDetailsView();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file AddContactDetails.fxml: " + e.getMessage());
        }
        try {
            loadCompanyDetailsView();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file AddCompanyDetails.fxml: " + e.getMessage());
        }

    }

    public void loadContactDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddContactDetails.fxml"));
        Parent contactDetailsRoot = loader.load();
        AddContactDetails addContactDetails = loader.getController();
        this.addContactDetails = addContactDetails;
        addContactDetails.setAddCustomerViewController(this);
        contactDetailsStage = new Stage();
        contactDetailsStage.initModality(Modality.APPLICATION_MODAL);
        contactDetailsStage.setTitle("Aggiungi info su indirizzo e recapito");
        contactDetailsStage.setScene(new Scene(contactDetailsRoot));

    }

    public void loadCompanyDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCompanyDetails.fxml"));
        Parent companyDetailsRoot = loader.load();
        AddCompanyDetails addCompanyDetails = loader.getController();
        this.addCompanyDetails = addCompanyDetails;
        addCompanyDetails.setAddCustomerViewController(this);
        companyDetailsStage = new Stage();
        companyDetailsStage.initModality(Modality.APPLICATION_MODAL);
        companyDetailsStage.setTitle("Aggiungi i dettagli dell'azienda");
        companyDetailsStage.setScene(new Scene(companyDetailsRoot));

    }

    public void disableCompanyData() {

        addCompanyDataButton.setDisable(!addCompanyDataButton.isDisable());
        if (addCompanyDataButton.isDisable()) {
            companyDetails.clear();
            addCompanyDetails.resetFields();
        }

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createCustomer(ActionEvent event) {

        try {
            verifyEmptyFields();
            Customer newCustomer = verifyIfCompanyInfoNull();
            addContactDetails.executeQuery();
            addCompanyDetails.executeQuery();
            customerManagementSceneController.addRow(newCustomer);
            clearCompanyDetails();
            clearContactDetails();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form
        }
        catch (RuntimeException missingParameters) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore parametri");
            alert.setContentText("Sono presenti dei problemi con alcuni parametri inseriti: " + missingParameters.getMessage());
            alert.showAndWait();
        }

    }

    public void verifyEmptyFields() throws RuntimeException {               // Verifica che non siano presenti campi vuoti tra quelli obbligatori

        int i = 0;
        while (i < 5) {
            switch (i) {
                case 0 -> {
                    if ("".equals(nameField.getText()))
                        throw new RuntimeException("Nome mancante");
                }
                case 1 -> {
                    if ("".equals(surnameField.getText()))
                        throw new RuntimeException("Cognome mancante");
                }
                case 2 -> {
                    if ("".equals(discountField.getText()))
                        throw new RuntimeException("Sconto mancante");
                }
                case 3 -> {
                    if (datePicker.getValue() == null)
                        throw new RuntimeException("Data mancante");
                }
                case 4 -> {
                    if ("".equals(fiscalCodeField.getText()))
                        throw new RuntimeException("Codice fiscale mancante");
                }
            }
            i++;
        }

    }

    public Customer verifyIfCompanyInfoNull() {             // Controlla se sono state inserite info sull'azienda o meno (nel caso di privati)

        if (!companyDetails.isEmpty() && !privateCheckBox.isSelected()) {
            return new Customer(Integer.parseInt(discountField.getText()), nameField.getText(), surnameField.getText(), datePicker.getValue().toString(), fiscalCodeField.getText(),
                    companyDetails.get(0), companyDetails.get(1), contactDetails.get(0), contactDetails.get(1), contactDetails.get(2));
        }
        else {
            return new Customer(Integer.parseInt(discountField.getText()), nameField.getText(), surnameField.getText(), datePicker.getValue().toString(), fiscalCodeField.getText(),
                    null, null, contactDetails.get(0), contactDetails.get(1), contactDetails.get(2));
        }

    }

    public void setCustomerManagementSceneController(CustomerManagementSceneController customerManagementSceneController) {
        this.customerManagementSceneController = customerManagementSceneController;
    }

    public void viewAddCompanyDetails(ActionEvent ignoredEvent) {              // Apre la finestra di aggiunta dati sull'azienda
        companyDetailsStage.show();
    }

    public void viewAddContactDetails(ActionEvent ignoredEvent) {           // FIXME: Se già aperto nella stessa sessione, mostrare i soliti dati
        contactDetailsStage.show();
    }

    public void setCompanyDetails(String P_IVA, String ragione_sociale) {

        companyDetails.add(P_IVA);
        companyDetails.add(ragione_sociale);

    }

    public void setContactDetails(String address, String CAP, String civicNumber) {

        contactDetails.add(address);
        contactDetails.add(CAP);
        contactDetails.add(civicNumber);

    }

    public void clearContactDetails() {
        contactDetails.clear();
    }

    public void clearCompanyDetails() {
        companyDetails.clear();
    }

}
