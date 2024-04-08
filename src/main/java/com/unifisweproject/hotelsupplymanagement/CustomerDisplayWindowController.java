package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
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

public class CustomerDisplayWindowController implements Initializable {
    @FXML
    public Button addCompanyDataButton;
    @FXML
    public CheckBox privateCheckBox;
    @FXML
    public Label codeLabel;
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField discountField;
    @FXML
    private TextField fiscalCodeField;
    private Stage contactDetailsStage;
    private Stage companyDetailsStage;
    private Customer displayedCustomer;
    private ContactDetailsModifyWindowController contactDetailsModifyWindowController = null;
    private CompanyDetailsModifyWindowController companyDetailsModifyWindowController = null;
    private CustomerManagementWindowController customerManagementWindowController;

    private final ArrayList<String> contactDetails = new ArrayList<>();
    private final ArrayList<String> companyDetails = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setFormatters();
        Platform.runLater(() -> {
            addCustomerInfo();
            try {
                loadModifyContactDetailsView();
            }
            catch (IOException e) {
                System.err.println("Errore durante l'apertura del file ContactDetailsModifyWindowController.fxml: " + e.getMessage());
            }
            try {
                if (displayedCustomer.getP_IVA() != null && displayedCustomer.getRagione_sociale() != null) {               // Se il cliente è un'azienda, abilita il pulsante per l'aggiunta dei dati aziendali
                    loadModifyCompanyDetailsView();
                    privateCheckBox.setSelected(false);
                }
                else {
                    addCompanyDataButton.setDisable(true);
                    privateCheckBox.setSelected(true);
                }
            }
            catch (IOException e) {
                System.err.println("Errore durante l'apertura del file CompanyDetailsModifyWindowController.fxml: " + e.getMessage());
            }
        });

    }

    public void setFormatters() {

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
        fiscalCodeField.setTextFormatter(fiscalCodeFormatter);

        UnaryOperator<TextFormatter.Change> filterDiscount = change -> {             // Creazione del Formatter per lo sconto: contiene unicamente numeri ed al massimo 2 cifre
            String text = change.getText();
            if (text.matches("[0-9]*") && change.getControlNewText().length() <= maxDiscountCharacters) {
                return change;
            }
            return null;
        };
        TextFormatter<String> discountFormatter = new TextFormatter<>(filterDiscount);
        discountField.setTextFormatter(discountFormatter);

    }

    public void addCustomerInfo() {                                                         // Metodo per l'aggiunta delle informazioni del cliente nei TextField

        codeLabel.setText("Dati cliente n°" + displayedCustomer.getCodice_cliente());
        nameField.setText(displayedCustomer.getNome());
        surnameField.setText(displayedCustomer.getCognome());
        fiscalCodeField.setText(displayedCustomer.getCodice_fiscale());
        discountField.setText(Integer.toString(displayedCustomer.getSconto()));
        datePicker.setValue(LocalDate.parse(displayedCustomer.getData_inserimento()));

    }

    public void loadModifyContactDetailsView() throws IOException {         // Metodo per il caricamento della finestra di modifica dei dati di contatto (non vien visualizzata subito)

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactDetailsModifyWindow.fxml"));
        Parent contactDetailsRoot = loader.load();
        ContactDetailsModifyWindowController contactDetailsModifyWindowController = loader.getController();
        this.contactDetailsModifyWindowController = contactDetailsModifyWindowController;
        contactDetailsModifyWindowController.setCustomerViewController(this);
        contactDetailsStage = new Stage();
        contactDetailsStage.initModality(Modality.APPLICATION_MODAL);
        contactDetailsStage.setTitle("Aggiungi info su indirizzo e recapito");
        contactDetailsStage.setResizable(false);
        contactDetailsStage.getIcons().add(HotelSupplyManagementMain.icon);
        contactDetailsStage.setScene(new Scene(contactDetailsRoot));

    }

    public void loadModifyCompanyDetailsView() throws IOException {                 // Metodo per il caricamento della finestra di modifica dei dati aziendali (non viene visualizzata subito)

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CompanyDetailsModifyWindow.fxml"));
        Parent companyDetailsRoot = loader.load();
        CompanyDetailsModifyWindowController companyDetailsModifyWindowController = loader.getController();
        this.companyDetailsModifyWindowController = companyDetailsModifyWindowController;
        companyDetailsModifyWindowController.setCustomerViewController(this);
        companyDetailsStage = new Stage();
        companyDetailsStage.initModality(Modality.APPLICATION_MODAL);
        companyDetailsStage.setTitle("Aggiungi info su azienda");
        companyDetailsStage.setResizable(false);
        companyDetailsStage.getIcons().add(HotelSupplyManagementMain.icon);
        companyDetailsStage.setScene(new Scene(companyDetailsRoot));

    }

    public void viewModifyContactDetails(ActionEvent ignoredEvent) {
        contactDetailsStage.show();
    }

    public void viewModifyCompanyDetails(ActionEvent ignoredEvent) {
        companyDetailsStage.show();
    }

    public void setDisplayedCustomer(Customer displayedCustomer) {
        this.displayedCustomer = displayedCustomer;
    }

    public void modifyCustomer(ActionEvent event) {                 // Una volta validati i dati inseriti, si procede con la modifica del cliente

        try {
            verifyEmptyFields();
            if (companyDetailsModifyWindowController != null) {
                companyDetailsModifyWindowController.executeQuery();
            }
            contactDetailsModifyWindowController.executeQuery(displayedCustomer.getIndirizzo(), displayedCustomer.getCivico(), displayedCustomer.getCAP());
            if (!contactDetails.isEmpty()) {
                displayedCustomer.setIndirizzo(contactDetails.get(0));
                displayedCustomer.setCivico(contactDetails.get(2));
                displayedCustomer.setCAP(contactDetails.get(1));
            }
            if (!companyDetails.isEmpty()) {
                displayedCustomer.setP_IVA(companyDetails.get(0));
                displayedCustomer.setRagione_sociale(companyDetails.get(1));
            }
            displayedCustomer.setNome(nameField.getText());
            displayedCustomer.setCognome(surnameField.getText());
            displayedCustomer.setCodice_fiscale(fiscalCodeField.getText());
            displayedCustomer.setSconto(Integer.parseInt(discountField.getText()));
            displayedCustomer.setData_inserimento(datePicker.getValue().toString());
            customerManagementWindowController.modifyRow(displayedCustomer);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form
        }
        catch (RuntimeException emptyFields) {
            System.out.println(emptyFields.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi vuoti o errati");
            alert.setContentText("Riempire tutti i campi o controllare che siano stati inseriti correttamente");
            alert.showAndWait();
        }

    }

    public void verifyEmptyFields() {                   // TODO: Riportare questa implementazione anche da altre parti del codice
        if ("".equals(nameField.getText()) || "".equals(surnameField.getText()) || "".equals(fiscalCodeField.getText()) || "".equals(discountField.getText()) || datePicker.getValue() == null) {
            throw new RuntimeException("Campi vuoti");
        }
    }

    public void setCompanyDetails(String P_IVA, String Ragione_Sociale) {

        companyDetails.clear();                     // Prima di effettuare la modifica rendo di nuovo vuota la lista di parametri
        companyDetails.add(P_IVA);
        companyDetails.add(Ragione_Sociale);

    }

    public void setContactDetails(String address, String CAP, String civicNumber) {

        contactDetails.clear();
        contactDetails.add(address);
        contactDetails.add(CAP);
        contactDetails.add(civicNumber);

    }

    public void closeCustomerView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setCustomerManagementSceneController(CustomerManagementWindowController customerManagementWindowController) {
        this.customerManagementWindowController = customerManagementWindowController;
    }

    public Customer getDisplayedCustomer() {
        return displayedCustomer;
    }

}
