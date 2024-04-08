package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
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

public class SupplierDisplayWindowController implements Initializable {

    @FXML
    private DatePicker datePicker;
    private Stage companyDetailsStage;
    private Stage contactDetailsStage;
    private Supplier displayedSupplier;
    private SupplierManagementWindowController supplierManagementWindowController;
    private CompanyDetailsModifyWindowController companyDetailsModifyWindowController;
    private ContactDetailsModifyWindowController contactDetailsModifyWindowController;
    private final ArrayList<String> companyDetails = new ArrayList<>();
    private final ArrayList<String> contactDetails = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            addSupplierInfo();
            try {
                loadContactDetailsView();
            }
            catch (IOException e) {
                System.err.println("Errore durante l'apertura del file ContactDetailsModifyWindow.fxml: " + e.getMessage());
            }
            try {
                loadCompanyDetailsView();
            }
            catch (IOException e) {
                System.err.println("Errore durante l'apertura del file CompanyDetailsModifyWindow.fxml: " + e.getMessage());
            }
        });

    }

    public void loadContactDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ContactDetailsModifyWindow.fxml"));
        Parent contactDetailsRoot = loader.load();
        ContactDetailsModifyWindowController contactDetailsModifyWindowController = loader.getController();
        this.contactDetailsModifyWindowController = contactDetailsModifyWindowController;
        contactDetailsModifyWindowController.setSupplierViewController(this);
        contactDetailsStage = new Stage();
        contactDetailsStage.initModality(Modality.APPLICATION_MODAL);
        contactDetailsStage.setTitle("Modifica info su indirizzo e recapito");
        contactDetailsStage.setResizable(false);
        contactDetailsStage.getIcons().add(HotelSupplyManagementMain.icon);
        contactDetailsStage.setScene(new Scene(contactDetailsRoot));

    }

    public void loadCompanyDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CompanyDetailsModifyWindow.fxml"));
        Parent companyDetailsRoot = loader.load();
        CompanyDetailsModifyWindowController companyDetailsModifyWindowController = loader.getController();
        this.companyDetailsModifyWindowController = companyDetailsModifyWindowController;
        companyDetailsModifyWindowController.setSupplierViewController(this);
        companyDetailsStage = new Stage();
        companyDetailsStage.initModality(Modality.APPLICATION_MODAL);
        companyDetailsStage.setTitle("Modifica i dettagli dell'azienda");
        companyDetailsStage.setResizable(false);
        companyDetailsStage.getIcons().add(HotelSupplyManagementMain.icon);
        companyDetailsStage.setScene(new Scene(companyDetailsRoot));

    }

    public void addSupplierInfo() {datePicker.setValue(LocalDate.parse(displayedSupplier.getData_inserimento())); }

    public void setDisplayedSupplier(Supplier displayedSupplier) {
        this.displayedSupplier = displayedSupplier;
    }

    public void modifySupplier(ActionEvent event) {

        try {
            verifyEmptyFields();
            companyDetailsModifyWindowController.executeQuery();
            contactDetailsModifyWindowController.executeQuery(displayedSupplier.getIndirizzo(), displayedSupplier.getCivico(), displayedSupplier.getCAP());

            if (!contactDetails.isEmpty()) {
                displayedSupplier.setIndirizzo(contactDetails.get(0));
                displayedSupplier.setCivico(contactDetails.get(2));         // NOTA! Qui si inserisce prima il 2 per via di una discrepanza tra l'ordine delle colonne CAP e Civico inverso tra Fornitore e Recapito
                displayedSupplier.setCAP(contactDetails.get(1));
            }
            if (!companyDetails.isEmpty()) {
                displayedSupplier.setRagione_sociale(companyDetails.get(1));
                displayedSupplier.setP_IVA(companyDetails.get(0));
            }

            displayedSupplier.setData_inserimento(datePicker.getValue().toString());
            supplierManagementWindowController.modifyRow(displayedSupplier);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

        }
        catch (RuntimeException errorWithParameters) {
            System.err.println("Errore con alcuni parametri: "+ errorWithParameters.getMessage());
        }

    }

    public void verifyEmptyFields() throws RuntimeException {
        if(datePicker.getValue() == null)
            throw new RuntimeException("Data mancante");
    }

    public void closeSupplierView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setSupplierManagementSceneController(SupplierManagementWindowController supplierManagementWindowController) {
        this.supplierManagementWindowController = supplierManagementWindowController;
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
    public Supplier getDisplayedSupplier() {
        return displayedSupplier;
    }
    public void viewModifyCompanyDetails(ActionEvent ignoredEvent) {              // Apre la finestra di aggiunta dati sull'azienda
        companyDetailsStage.show();
    }

    public void viewModifyContactDetails(ActionEvent ignoredEvent) {
        contactDetailsStage.show();
    }

}
