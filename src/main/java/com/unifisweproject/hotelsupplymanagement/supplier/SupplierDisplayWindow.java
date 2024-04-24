package com.unifisweproject.hotelsupplymanagement.supplier;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class SupplierDisplayWindow implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private Label codeLabel;
    @FXML
    private Button modifyButton;
    @FXML
    private Button backButton;
    /*

    private final Supplier displayedSupplier;
    private final SupplierManagementController controller;

    public SupplierDisplayWindow(Item displayedItem) {
        this.controller = SupplierManagementController.getInstance();
        this.displayedSupplier = displayedSupplier;
    }

     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //addSupplierInfo();
        //modifyButton.setOnAction(this::modifySupplier);
        //backButton.setOnAction(this::closeSupplierView);

    }

    /*

    public void addSupplierInfo() {

        codeLabel.setText("Dati fornitore n°" + displayedSupplier.getCodice_fornitore());
        datePicker.setValue(LocalDate.parse(displayedSupplier.getData_inserimento()));

    }

    public void modifySupplier(ActionEvent event) {
        controller.modifyRow(event, displayedSupplier);
    }

    public void closeSupplierView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void loadContactDetailsView() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/contact/ContactDetailsModifyWindow.fxml"));
        Parent contactDetailsRoot = loader.load();
        ContactDetailsModifyWindow contactDetailsModifyWindowController = loader.getController();
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/company/CompanyDetailsModifyWindow.fxml"));
        Parent companyDetailsRoot = loader.load();
        CompanyDetailsModifyWindow companyDetailsModifyWindowController = loader.getController();
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

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void viewModifyCompanyDetails(ActionEvent ignoredEvent) {              // Apre la finestra di aggiunta dati sull'azienda
        companyDetailsStage.show();
    }

    public void viewModifyContactDetails(ActionEvent ignoredEvent) {
        contactDetailsStage.show();
    }

     */

}
