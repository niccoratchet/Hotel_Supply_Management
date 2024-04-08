package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class SupplierAddWindowController implements Initializable {


    @FXML
    private DatePicker datePicker;

    private SupplierManagementWindowController supplierManagementWindowController;
    private ContactDetailsAddWindowController contactDetailsAddWindowController = null;
    private CompanyDetailsAddWindowController companyDetailsAddWindowController = null;

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
            System.err.println("Errore durante l'apertura del file ContactDetailsAddWindow.fxml: " + e.getMessage());
        }
        try {
            loadCompanyDetailsView();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file CompanyDetailsAddWindow.fxml: " + e.getMessage());
        }
    }

    public void loadContactDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactDetailsAddWindow.fxml"));
        Parent contactDetailsRoot = loader.load();
        ContactDetailsAddWindowController contactDetailsAddWindowController = loader.getController();
        this.contactDetailsAddWindowController = contactDetailsAddWindowController;
        contactDetailsAddWindowController.setAddSupplierViewController(this);
        contactDetailsStage = new Stage();
        contactDetailsStage.initModality(Modality.APPLICATION_MODAL);
        contactDetailsStage.setTitle("Aggiungi info su indirizzo e recapito");
        contactDetailsStage.setResizable(false);
        contactDetailsStage.getIcons().add(HotelSupplyManagementMain.icon);
        contactDetailsStage.setScene(new Scene(contactDetailsRoot));

    }

    public void loadCompanyDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CompanyDetailsAddWindow.fxml"));
        Parent companyDetailsRoot = loader.load();
        CompanyDetailsAddWindowController companyDetailsAddWindowController = loader.getController();
        this.companyDetailsAddWindowController = companyDetailsAddWindowController;
        companyDetailsAddWindowController.setAddSupplierViewController(this);
        companyDetailsStage = new Stage();
        companyDetailsStage.initModality(Modality.APPLICATION_MODAL);
        companyDetailsStage.setTitle("Aggiungi i dettagli dell'azienda");
        companyDetailsStage.setResizable(false);
        companyDetailsStage.getIcons().add(HotelSupplyManagementMain.icon);
        companyDetailsStage.setScene(new Scene(companyDetailsRoot));

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createSupplier(ActionEvent event) {

        try {
            verifyEmptyFields();
            Supplier newSupplier = new Supplier(datePicker.getValue().toString(), companyDetails.get(0), companyDetails.get(1), contactDetails.get(0),
                    contactDetails.get(1), contactDetails.get(2));
            contactDetailsAddWindowController.executeQuery();
            companyDetailsAddWindowController.executeQuery();
            supplierManagementWindowController.addRow(newSupplier);
            clearCompanyDetails();
            clearContactDetails();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        }
        catch (RuntimeException errorWithParameters) {
            System.err.println("Errore con alcuni parametri: " + errorWithParameters.getMessage());
        }

    }

    public void verifyEmptyFields() throws RuntimeException {
        if(datePicker.getValue() == null)
            throw new RuntimeException("Data mancante");
    }

    public void setSupplierManagementSceneController(SupplierManagementWindowController supplierManagementWindowController) {
        this.supplierManagementWindowController = supplierManagementWindowController;
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
