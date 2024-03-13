package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class AddSupplierViewController implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField businessNameField;
    @FXML
    private TextField pivaField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField civicField;
    @FXML
    private TextField capField;

    private SupplierManagementSceneController supplierManagementSceneController;
    private AddContactDetails addContactDetails = null;         // TODO: Implementare collegamento tra le due view in modo da garantire la comunicazione
    private AddCompanyDetails addCompanyDetails = null;

    private final ArrayList<String> companyDetails = new ArrayList<>();
    private final ArrayList<String> contactDetails = new ArrayList<>();
    private Stage contactDetailsStage;
    private Stage companyDetailsStage;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());

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
        //addContactDetails.setAddCustomerViewController(this);
        contactDetailsStage = new Stage();
        contactDetailsStage.setTitle("Aggiungi info su indirizzo e recapito");
        contactDetailsStage.setScene(new Scene(contactDetailsRoot));

    }

    public void loadCompanyDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCompanyDetails.fxml"));
        Parent companyDetailsRoot = loader.load();
        AddCompanyDetails addCompanyDetails = loader.getController();
        this.addCompanyDetails = addCompanyDetails;
        //addCompanyDetails.setAddCustomerViewController(this);
        companyDetailsStage = new Stage();
        companyDetailsStage.setTitle("Aggiungi i dettagli dell'azienda");
        companyDetailsStage.setScene(new Scene(companyDetailsRoot));

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createSupplier(ActionEvent event) {
        Supplier newSupplier = new Supplier(datePicker.getValue().toString(), pivaField.getText(), businessNameField.getText(), addressField.getText(), civicField.getText(), capField.getText());
        supplierManagementSceneController.addRow(newSupplier);

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public Supplier verifyIfCompanyInfoNull() {             // Controlla se sono state inserite info sull'azienda o meno (nel caso di privati)

        return new Supplier(datePicker.getValue().toString(), companyDetails.get(0), companyDetails.get(1), contactDetails.get(0), contactDetails.get(1), contactDetails.get(2));

    }

    public void setSupplierManagementSceneController(SupplierManagementSceneController supplierManagementSceneController) {
        this.supplierManagementSceneController = supplierManagementSceneController;
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

    public void clearCompanyDetailsView() {
        companyDetails.clear();
    }
}
